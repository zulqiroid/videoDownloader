package com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.push

import android.os.Build
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

data class RegisterFcmTokenRequest(
    val token: String,
    val installationId: String,
    val appVersion: String,
    val language: String,
    val platform: String = "android",
    val deviceModel: String = "${Build.MANUFACTURER} ${Build.MODEL}",
    val androidVersion: String = Build.VERSION.RELEASE
)

interface PushNotificationRemoteDataSource {

    suspend fun registerToken(
        request: RegisterFcmTokenRequest
    )

    suspend fun unregisterToken(
        token: String
    )
}

class FirebasePushNotificationRemoteDataSource(
    private val firebaseFunctions: FirebaseFunctions
) : PushNotificationRemoteDataSource {

    override suspend fun registerToken(
        request: RegisterFcmTokenRequest
    ) {
        firebaseFunctions
            .getHttpsCallable(FUNCTION_REGISTER_FCM_TOKEN)
            .call(
                mapOf(
                    "token" to request.token,
                    "installationId" to request.installationId,
                    "appVersion" to request.appVersion,
                    "language" to request.language,
                    "platform" to request.platform,
                    "deviceModel" to request.deviceModel,
                    "androidVersion" to request.androidVersion
                )
            )
            .await()
    }

    override suspend fun unregisterToken(
        token: String
    ) {
        firebaseFunctions
            .getHttpsCallable(FUNCTION_UNREGISTER_FCM_TOKEN)
            .call(
                mapOf(
                    "token" to token
                )
            )
            .await()
    }

    companion object {
        private const val FUNCTION_REGISTER_FCM_TOKEN = "registerFcmToken"
        private const val FUNCTION_UNREGISTER_FCM_TOKEN = "unregisterFcmToken"
    }
}