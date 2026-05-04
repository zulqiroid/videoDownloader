package com.app.videodownloader.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.app.videodownloader.R
import com.app.videodownloader.core.notification.AppNotificationChannels

class AppNotificationManager(
    private val context: Context
) {

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )

        val downloadsChannel = NotificationChannel(
            AppNotificationChannels.DOWNLOADS_CHANNEL_ID,
            "Downloads",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Download status notifications"
        }

        val appUpdatesChannel = NotificationChannel(
            AppNotificationChannels.APP_UPDATES_CHANNEL_ID,
            "App Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "App update and feature notifications"
        }

        notificationManager.createNotificationChannel(downloadsChannel)
        notificationManager.createNotificationChannel(appUpdatesChannel)
    }

    fun showDownloadCompleteNotification(
        fileName: String
    ) {
        if (!canPostNotifications()) return

        val notification = NotificationCompat.Builder(
            context,
            AppNotificationChannels.DOWNLOADS_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_download_enable)
            .setContentTitle("Download Complete")
            .setContentText(fileName.ifBlank { "Your video is ready." })
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            AppNotificationChannels.DOWNLOAD_COMPLETE_NOTIFICATION_ID,
            notification
        )
    }

    fun showDownloadFailedNotification(
        reason: String
    ) {
        if (!canPostNotifications()) return

        val notification = NotificationCompat.Builder(
            context,
            AppNotificationChannels.DOWNLOADS_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_download_enable)
            .setContentTitle("Download Failed")
            .setContentText(reason.ifBlank { "Something went wrong while downloading." })
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            AppNotificationChannels.DOWNLOAD_FAILED_NOTIFICATION_ID,
            notification
        )
    }

    fun showAppUpdateNotification(
        title: String,
        message: String
    ) {
        if (!canPostNotifications()) return

        val notification = NotificationCompat.Builder(
            context,
            AppNotificationChannels.APP_UPDATES_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_info)
            .setContentTitle(title.ifBlank { "App Update" })
            .setContentText(message.ifBlank { "New features are available." })
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            AppNotificationChannels.APP_UPDATE_NOTIFICATION_ID,
            notification
        )
    }

    private fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}