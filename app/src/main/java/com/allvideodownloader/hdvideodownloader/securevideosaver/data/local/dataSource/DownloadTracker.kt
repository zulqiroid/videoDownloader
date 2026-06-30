package com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataSource

import android.content.Context

class DownloadTracker(
    private val context: Context,
) {

    /*@OptIn(UnstableApi::class)
    fun track(downloadId: Long): Flow<DownloadProgress> = flow {

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        while (true) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = dm.query(query)

            if (cursor != null && cursor.moveToFirst()) {

                val bytesDownloaded =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                val bytesTotal =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                val status =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))

                val progress = if (bytesTotal > 0) {
                    (bytesDownloaded * 100 / bytesTotal).toInt()
                } else 0

                emit(
                    DownloadProgress(
                        progress = progress,
                        status = status
                    )
                )

                if (status == DownloadManager.STATUS_SUCCESSFUL ||
                    status == DownloadManager.STATUS_FAILED
                ) {
                    cursor.close()
                    break
                }
            }

            cursor?.close()
            delay(500)
        }
    }*/
}