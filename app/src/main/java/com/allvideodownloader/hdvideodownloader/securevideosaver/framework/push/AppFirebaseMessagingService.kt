package com.allvideodownloader.hdvideodownloader.securevideosaver.framework.push

import android.util.Log
import com.allvideodownloader.hdvideodownloader.securevideosaver.BuildConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.push.PushMessage
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private val appNotificationManager: AppNotificationManager by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "FCM token: $token")
        }

        /*
         * Firebase Console mode:
         * No backend sync needed.
         * Use this token only for Firebase Console -> Send test message.
         */
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        val title = data["title"]
            ?: notification?.title
            ?: "Secure Video Saver"

        val body = data["body"]
            ?: notification?.body
            ?: "You have a new update."

        val pushMessage = PushMessage(
            title = title,
            body = body,
            imageUrl = data["image_url"],
            deepLink = data["deep_link"],
            type = data["type"],
            payload = data
        )

        appNotificationManager.showPushNotification(pushMessage)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.w(TAG, "Some pending FCM messages were deleted before delivery")
    }

    companion object {
        private const val TAG = "AppFirebaseMessaging"
    }
}