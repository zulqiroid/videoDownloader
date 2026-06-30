package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.push

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.push.PushNotificationRepository

class SyncFcmTokenUseCase(
    private val repository: PushNotificationRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.syncSavedFcmToken()
    }
}