package com.allvideodownloader.hdvideodownloader.securevideosaver.framework.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.core.MainActivity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.push.PushMessage
import kotlin.random.Random

interface AppNotificationManager {

    fun createNotificationChannels()

    fun showPushNotification(message: PushMessage)
}

class AndroidAppNotificationManager(
    private val context: Context
) : AppNotificationManager {

    override fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val generalChannel = NotificationChannel(
            GENERAL_CHANNEL_ID,
            "General notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "App updates, tips, and important alerts"
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(generalChannel)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun showPushNotification(message: PushMessage) {
        if (!canPostNotifications()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

            message.deepLink
                ?.takeIf { it.isNotBlank() }
                ?.let { deepLink ->
                    data = deepLink.toUri()
                }

            putExtra(EXTRA_PUSH_TYPE, message.type)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, GENERAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_main)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message.body)
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(Random.nextInt(), notification)
    }

    private fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val GENERAL_CHANNEL_ID = "general_notifications"

        private const val EXTRA_PUSH_TYPE = "extra_push_type"
    }
}