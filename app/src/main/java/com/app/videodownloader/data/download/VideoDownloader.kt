package com.app.videodownloader.data.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
 import io.ktor.client.HttpClient
 import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.host
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.File
import java.io.FileOutputStream

class VideoDownloader(
    private val context: Context,
    private val client: HttpClient
) {


    suspend fun downloadVideo(url: String) {

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

   /* suspend fun downloadVideo(url: String) {
        try {
            val fileName = "video_${System.currentTimeMillis()}.mp4"
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )

            val response = client.get(url) {
                // 1. Completely clear Ktor's internal fingerprint
                headers.clear()

                // 2. Exact Postman Headers
                header("User-Agent", "PostmanRuntime/7.32.3")
                header("Cache-Control", "no-cache")
                header("Host", Uri.parse(url).host)
                header("Connection", "keep-alive")
                header("Accept-Encoding", "gzip, deflate, br")
            }

            if (response.status.value in 200..299) {
                response.bodyAsChannel().toInputStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                println("DOWNLOAD SUCCESS: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            // If it STILL resets, it's an IP-level block or TLS fingerprint issue
            e.printStackTrace()
        }
    }*/

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