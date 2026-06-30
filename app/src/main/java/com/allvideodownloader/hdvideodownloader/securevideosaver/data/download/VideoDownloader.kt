package com.allvideodownloader.hdvideodownloader.securevideosaver.data.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import io.ktor.client.HttpClient

class VideoDownloader(
    private val context: Context,
    private val client: HttpClient
) {


    suspend fun downloadVideo(url: String) {

        Log.d("video to be download","the url is downloading : $url ")

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

        val id = downloadManager.enqueue(request)

        checkDownloadStatus(id)
    }

    fun checkDownloadStatus(downloadId: Long) {

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)

        val cursor = dm.query(query)

        if (cursor.moveToFirst()) {

            val status = cursor.getInt(
                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
            )

            val reason = cursor.getInt(
                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)
            )

            println("DOWNLOAD STATUS: $status | REASON: $reason")
        }

        cursor.close()
    }
}