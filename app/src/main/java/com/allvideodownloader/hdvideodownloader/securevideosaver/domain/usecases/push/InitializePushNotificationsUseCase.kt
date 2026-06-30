package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.push

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.push.PushNotificationRepository

class InitializePushNotificationsUseCase(
    private val repository: PushNotificationRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository
            .fetchCurrentFcmToken()
            .mapCatching {
                repository.syncSavedFcmToken().getOrThrow()
                repository.subscribeDefaultTopics().getOrThrow()
            }
    }
}