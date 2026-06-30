package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.push

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.push.PushNotificationRepository

class SaveFcmTokenUseCase(
    private val repository: PushNotificationRepository
) {
    suspend operator fun invoke(
        token: String
    ): Result<Unit> {
        return repository.saveFcmToken(token)
    }
}