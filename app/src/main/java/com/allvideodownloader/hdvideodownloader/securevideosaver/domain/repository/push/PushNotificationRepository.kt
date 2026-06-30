package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.push

interface PushNotificationRepository {

    suspend fun fetchCurrentFcmToken(): Result<String>

    suspend fun saveFcmToken(
        token: String
    ): Result<Unit>

    suspend fun syncSavedFcmToken(): Result<Unit>

    suspend fun subscribeDefaultTopics(): Result<Unit>

    suspend fun clearFcmToken(): Result<Unit>
}