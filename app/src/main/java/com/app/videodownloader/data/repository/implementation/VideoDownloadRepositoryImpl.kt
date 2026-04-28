package com.app.videodownloader.data.repository.implementation

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.app.videodownloader.domain.repository.VideoDownloadRepository

class VideoDownloadRepositoryImpl(
    private val context: Context
) : VideoDownloadRepository {

    override suspend fun downloadVideo(url: String) {

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading Video")
            .setDescription("Please wait...")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "video_${System.currentTimeMillis()}.mp4"
            )

        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadManager.enqueue(request)
    }
}