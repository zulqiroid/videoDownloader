package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.push

import android.content.Context
import android.util.Log
import com.allvideodownloader.hdvideodownloader.securevideosaver.BuildConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.push.FcmTokenLocalDataSource
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.push.PushNotificationRemoteDataSource
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.push.RegisterFcmTokenRequest
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.push.PushNotificationRepository
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import java.util.Locale

class PushNotificationRepositoryImpl(
    private val context: Context,
    private val firebaseMessaging: FirebaseMessaging,
    private val firebaseInstallations: FirebaseInstallations,
    private val localDataSource: FcmTokenLocalDataSource,
    private val remoteDataSource: PushNotificationRemoteDataSource
) : PushNotificationRepository {

    override suspend fun fetchCurrentFcmToken(): Result<String> {
        return runCatching {
            val token = firebaseMessaging.token.await()

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "FCM token: $token")
            }

            localDataSource.saveToken(token)

            token
        }
    }

    override suspend fun saveFcmToken(
        token: String
    ): Result<Unit> {
        return runCatching {
            localDataSource.saveToken(token)
        }
    }

    override suspend fun syncSavedFcmToken(): Result<Unit> {
        return runCatching {
            val token = localDataSource.getToken()

            if (token.isNullOrBlank()) {
                return@runCatching
            }

            val installationId = firebaseInstallations.id.await()

            remoteDataSource.registerToken(
                RegisterFcmTokenRequest(
                    token = token,
                    installationId = installationId,
                    appVersion = context.appVersionName(),
                    language = Locale.getDefault().language
                )
            )

            localDataSource.markTokenSynced(token)
        }
    }

    override suspend fun subscribeDefaultTopics(): Result<Unit> {
        return runCatching {
            firebaseMessaging
                .subscribeToTopic(TOPIC_ANDROID_USERS)
                .await()
        }
    }

    override suspend fun clearFcmToken(): Result<Unit> {
        return runCatching {
            val token = localDataSource.getToken()

            if (!token.isNullOrBlank()) {
                remoteDataSource.unregisterToken(token)
            }

            localDataSource.clearToken()
        }
    }

    private fun Context.appVersionName(): String {
        return runCatching {
            packageManager
                .getPackageInfo(packageName, 0)
                .versionName ?: "unknown"
        }.getOrDefault("unknown")
    }

    companion object {
        private const val TAG = "PushNotificationRepo"
        private const val TOPIC_ANDROID_USERS = "android_users"
    }
}