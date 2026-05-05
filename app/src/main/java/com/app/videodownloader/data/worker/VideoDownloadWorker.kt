package com.app.videodownloader.data.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.app.videodownloader.R
import com.app.videodownloader.core.notification.AppNotificationChannels
import com.app.videodownloader.data.notification.DownloadNotificationDispatcher
import com.app.videodownloader.domain.model.DownloadItem
import com.app.videodownloader.domain.model.DownloadProgressStore
import com.app.videodownloader.domain.model.DownloadStatus
 import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

class VideoDownloadWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(
    appContext = context,
    params = workerParameters
), KoinComponent {

    private val okHttpClient: OkHttpClient by inject()
    private val progressStore: DownloadProgressStore by inject()
    private val notificationDispatcher: DownloadNotificationDispatcher by inject()

    override suspend fun doWork(): Result {
        val id = inputData.getLong(KEY_ID, INVALID_ID)
        val url = inputData.getString(KEY_URL).orEmpty()
        val fileName = inputData.getString(KEY_FILE_NAME).orEmpty()

        if (id == INVALID_ID || url.isBlank() || fileName.isBlank()) {
            return Result.failure()
        }

        setForeground(
            createForegroundInfo(
                id = id,
                fileName = fileName,
                progress = 0
            )
        )

        return try {
            withContext(Dispatchers.IO) {
                runDownload(
                    id = id,
                    url = url,
                    fileName = fileName
                )
            }
        } catch (cancellation: CancellationException) {
            /*
             * Important:
             * Pause uses WorkManager cancellation.
             * Cancellation is not a failed download.
             */
            throw cancellation
        } catch (throwable: Throwable) {
            markFailed(
                id = id,
                url = url,
                fileName = fileName,
                reason = throwable.message ?: "Download failed"
            )

            Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val id = inputData.getLong(KEY_ID, System.currentTimeMillis())
        val fileName = inputData.getString(KEY_FILE_NAME).orEmpty()

        return createForegroundInfo(
            id = id,
            fileName = fileName.ifBlank { "Downloading video" },
            progress = 0
        )
    }

    private suspend fun runDownload(
        id: Long,
        url: String,
        fileName: String,
    ): Result {
        val downloadFolder = getDownloadFolder()

        val finalFile = File(
            downloadFolder,
            fileName
        )

        val tempFile = File(
            downloadFolder,
            "$fileName.part"
        )

        val existingBytes = tempFile
            .takeIf { it.exists() }
            ?.length()
            ?: 0L

        val requestBuilder = Request.Builder()
            .url(url)

        if (existingBytes > 0L) {
            requestBuilder.header(
                name = RANGE_HEADER,
                value = "bytes=$existingBytes-"
            )
        }

        val request = requestBuilder.build()

        okHttpClient
            .newCall(request)
            .execute()
            .use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException(
                        "Server error: ${response.code}"
                    )
                }

                val supportsResume = response.code == HTTP_PARTIAL_CONTENT

                val shouldAppend = existingBytes > 0L && supportsResume

                if (existingBytes > 0L && !supportsResume) {
                    tempFile.delete()
                }

                val startBytes = if (shouldAppend) {
                    existingBytes
                } else {
                    0L
                }

                val responseContentLength = response.body.contentLength()

                val totalBytes = when {
                    responseContentLength <= 0L -> 0L
                    shouldAppend -> startBytes + responseContentLength
                    else -> responseContentLength
                }

                saveProgress(
                    id = id,
                    url = url,
                    fileName = fileName,
                    finalFile = finalFile,
                    progress = calculateProgress(
                        downloadedBytes = startBytes,
                        totalBytes = totalBytes
                    ),
                    status = DownloadStatus.DOWNLOADING,
                    downloadedBytes = startBytes,
                    totalBytes = totalBytes,
                    speedBytesPerSec = 0L,
                    lastEtaSeconds = -1L
                )

                response.body.byteStream().use { inputStream ->
                    RandomAccessFile(tempFile, RANDOM_ACCESS_MODE).use { outputFile ->
                        outputFile.seek(startBytes)

                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var downloadedBytes = startBytes

                        var lastProgressTime = System.currentTimeMillis()
                        var lastProgressBytes = downloadedBytes

                        while (true) {
                            coroutineContext.ensureActive()

                            val read = inputStream.read(buffer)

                            if (read == END_OF_STREAM) {
                                break
                            }

                            outputFile.write(
                                buffer,
                                0,
                                read
                            )

                            downloadedBytes += read

                            val now = System.currentTimeMillis()

                            if (now - lastProgressTime >= PROGRESS_UPDATE_INTERVAL_MS) {
                                val bytesDiff = downloadedBytes - lastProgressBytes
                                val timeDiff = now - lastProgressTime

                                val speedBytesPerSec = if (timeDiff > 0L) {
                                    (bytesDiff * 1000L) / timeDiff
                                } else {
                                    0L
                                }

                                val remainingBytes = totalBytes - downloadedBytes

                                val etaSeconds = if (speedBytesPerSec > 0L && totalBytes > 0L) {
                                    remainingBytes / speedBytesPerSec
                                } else {
                                    -1L
                                }

                                val progress = calculateProgress(
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes
                                )

                                saveProgress(
                                    id = id,
                                    url = url,
                                    fileName = fileName,
                                    finalFile = finalFile,
                                    progress = progress,
                                    status = DownloadStatus.DOWNLOADING,
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes,
                                    speedBytesPerSec = speedBytesPerSec,
                                    lastEtaSeconds = etaSeconds
                                )

                                setForeground(
                                    createForegroundInfo(
                                        id = id,
                                        fileName = fileName,
                                        progress = progress
                                    )
                                )

                                lastProgressTime = now
                                lastProgressBytes = downloadedBytes
                            }
                        }
                    }
                }
            }

        if (finalFile.exists()) {
            finalFile.delete()
        }

        val renamed = tempFile.renameTo(finalFile)

        if (!renamed) {
            throw IllegalStateException("Unable to save downloaded file.")
        }

        progressStore.upsert(
            DownloadItem(
                id = id,
                url = url,
                fileName = fileName,
                filePath = finalFile.absolutePath,
                progress = 100,
                status = DownloadStatus.SUCCESS,
                downloadedBytes = finalFile.length(),
                totalBytes = finalFile.length(),
                speedBytesPerSec = 0L,
                lastEtaSeconds = 0L,
                workId = this.id.toString()
            )
        )

        notificationDispatcher.notifyDownloadCompleted(
            fileName = fileName
        )

        return Result.success()
    }

    private suspend fun markFailed(
        id: Long,
        url: String,
        fileName: String,
        reason: String,
    ) {
        val finalFile = File(
            getDownloadFolder(),
            fileName
        )

        val tempFile = File(
            getDownloadFolder(),
            "$fileName.part"
        )

        progressStore.upsert(
            DownloadItem(
                id = id,
                url = url,
                fileName = fileName,
                filePath = finalFile.absolutePath,
                progress = calculateProgress(
                    downloadedBytes = tempFile.length(),
                    totalBytes = 0L
                ),
                status = DownloadStatus.FAILED,
                downloadedBytes = tempFile.length(),
                totalBytes = 0L,
                speedBytesPerSec = 0L,
                lastEtaSeconds = -1L,
                workId = this.id.toString()
            )
        )

        notificationDispatcher.notifyDownloadFailed(
            reason = reason
        )
    }

    private suspend fun saveProgress(
        id: Long,
        url: String,
        fileName: String,
        finalFile: File,
        progress: Int,
        status: DownloadStatus,
        downloadedBytes: Long,
        totalBytes: Long,
        speedBytesPerSec: Long,
        lastEtaSeconds: Long,
    ) {
        progressStore.upsert(
            DownloadItem(
                id = id,
                url = url,
                fileName = fileName,
                filePath = finalFile.absolutePath,
                progress = progress,
                status = status,
                downloadedBytes = downloadedBytes,
                totalBytes = totalBytes,
                speedBytesPerSec = speedBytesPerSec,
                lastEtaSeconds = lastEtaSeconds,
                workId = this.id.toString()
            )
        )
    }

    private fun createForegroundInfo(
        id: Long,
        fileName: String,
        progress: Int,
    ): ForegroundInfo {
        val notification = NotificationCompat.Builder(
            context,
            AppNotificationChannels.DOWNLOADS_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_download_enable)
            .setContentTitle("Downloading")
            .setContentText(fileName)
            .setProgress(
                100,
                progress.coerceIn(0, 100),
                false
            )
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .build()

        val notificationId = id.hashCode()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                notificationId,
                notification
            )
        }
    }

    private fun getDownloadFolder(): File {
        return File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ),
            DOWNLOAD_FOLDER_NAME
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private fun calculateProgress(
        downloadedBytes: Long,
        totalBytes: Long,
    ): Int {
        if (totalBytes <= 0L) return 0

        return ((downloadedBytes * 100L) / totalBytes)
            .toInt()
            .coerceIn(0, 100)
    }

    companion object {
        const val KEY_ID = "download_id"
        const val KEY_URL = "download_url"
        const val KEY_FILE_NAME = "download_file_name"

        private const val INVALID_ID = -1L
        private const val DOWNLOAD_FOLDER_NAME = "VideoDownloader"
        private const val RANGE_HEADER = "Range"
        private const val RANDOM_ACCESS_MODE = "rw"
        private const val END_OF_STREAM = -1
        private const val HTTP_PARTIAL_CONTENT = 206
        private const val PROGRESS_UPDATE_INTERVAL_MS = 800L
    }
}