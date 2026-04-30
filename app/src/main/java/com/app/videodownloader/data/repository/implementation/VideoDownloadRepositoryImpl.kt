package com.app.videodownloader.data.repository.implementation

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.app.videodownloader.domain.model.DownloadItem
import com.app.videodownloader.domain.model.DownloadStatus
import com.app.videodownloader.domain.repository.VideoDownloadRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


class VideoDownloadRepositoryImpl(
    private val context: Context
) : VideoDownloadRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val downloads = MutableStateFlow<List<DownloadItem>>(emptyList())

    override fun observeDownloads(): Flow<List<DownloadItem>> = downloads

    override suspend fun startDownload(url: String): Long {

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val fileName = "video_${System.currentTimeMillis()}.mp4"

        val request = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "VideoDownloader/$fileName"
            )

        val id = dm.enqueue(request)

        val filePath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "VideoDownloader/$fileName"
        ).absolutePath

        trackDownload(id, url, fileName, filePath)

        return id
    }

    override suspend fun getAllDownloadedFiles(): List<DownloadItem> {

        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "VideoDownloader"
        )

        if (!folder.exists()) return emptyList()

        return folder.listFiles()?.map { file ->
            DownloadItem(
                id = file.absolutePath.hashCode().toLong(),
                url = file.absolutePath,
                fileName = file.name,
                filePath = file.absolutePath,
                progress = 100,
                status = DownloadStatus.SUCCESS,
                downloadedBytes = file.length(),
                totalBytes = file.length()
            )
        } ?: emptyList()
    }

    override suspend fun cancelDownload(id: Long) {

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        // cancel download
        dm.remove(id)

        // remove from flow
        downloads.update { current ->
            current.filterNot { it.id == id }
        }
    }

/*    override suspend fun deleteDownload(id: Long) {

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val item = downloads.value.firstOrNull { it.id == id }

        dm.remove(id)

        item?.filePath?.let {
            val file = File(it)
            if (file.exists()) file.delete()
        }

        downloads.update { current ->
            current.filterNot { it.id == id }
        }
    }*/

    private fun trackDownload(
        id: Long,
        url: String,
        fileName: String,
        filePath: String
    ) {
        scope.launch {

            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            var lastBytes = 0L
            var lastTime = System.currentTimeMillis()
            var lastSpeed = 0L
            var lastEta = -1L

            while (true) {

                val cursor = dm.query(
                    DownloadManager.Query().setFilterById(id)
                )

                cursor?.use {

                    if (it.moveToFirst()) {

                        val downloaded =
                            it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                        val total =
                            it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                        val statusInt =
                            it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))

                        val now = System.currentTimeMillis()

                        val timeDiff = now - lastTime
                        val bytesDiff = downloaded - lastBytes

                        val rawSpeed = if (timeDiff > 0) {
                            (bytesDiff * 1000) / timeDiff
                        } else 0

                        val speed = if (rawSpeed > 0) {
                            lastSpeed = rawSpeed
                            rawSpeed
                        } else {
                            lastSpeed
                        }

                        lastBytes = downloaded
                        lastTime = now

                        val progress =
                            if (total > 0) ((downloaded * 100) / total).toInt() else 0

                        val remaining = total - downloaded

                        val eta = if (speed > 0) {
                            val calculated = remaining / speed
                            lastEta = calculated
                            calculated
                        } else {
                            lastEta
                        }

                        val item = DownloadItem(
                            id = id,
                            url = url,
                            fileName = fileName,
                            filePath = filePath,
                            progress = progress,
                            status = mapStatus(statusInt),
                            downloadedBytes = downloaded,
                            totalBytes = total,
                            speedBytesPerSec = speed,
                            lastEtaSeconds = eta
                        )

                        downloads.update { current ->
                            current.filterNot { it.id == id } + item
                        }

                        if (
                            statusInt == DownloadManager.STATUS_SUCCESSFUL ||
                            statusInt == DownloadManager.STATUS_FAILED
                        ) break
                    }
                }

                delay(800) // smoother polling
            }
        }
    }

    private fun mapStatus(status: Int): DownloadStatus {
        return when (status) {
            DownloadManager.STATUS_RUNNING -> DownloadStatus.DOWNLOADING
            DownloadManager.STATUS_PAUSED -> DownloadStatus.PAUSED
            DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.SUCCESS
            else -> DownloadStatus.FAILED
        }
    }
}