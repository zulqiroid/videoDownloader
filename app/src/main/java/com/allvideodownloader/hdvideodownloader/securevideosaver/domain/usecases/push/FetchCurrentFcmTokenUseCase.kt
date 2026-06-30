package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.push

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.push.PushNotificationRepository

class FetchCurrentFcmTokenUseCase(
    private val repository: PushNotificationRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.fetchCurrentFcmToken()
    }
}