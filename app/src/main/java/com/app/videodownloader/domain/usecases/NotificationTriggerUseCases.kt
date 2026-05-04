package com.app.videodownloader.domain.usecases

 import com.app.videodownloader.data.notification.AppNotificationManager
import com.app.videodownloader.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.flow.first

data class NotificationTriggerUseCases(
    val showDownloadCompleteNotificationUseCase: ShowDownloadCompleteNotificationUseCase,
    val showDownloadFailedNotificationUseCase: ShowDownloadFailedNotificationUseCase,
    val showAppUpdateNotificationUseCase: ShowAppUpdateNotificationUseCase
)

class ShowDownloadCompleteNotificationUseCase(
    private val repository: NotificationSettingsRepository,
    private val notificationManager: AppNotificationManager
) {
    suspend operator fun invoke(
        fileName: String
    ) {
        val settings = repository.observeNotificationSettings().first()

        if (!settings.downloadCompleteEnabled) return

        notificationManager.showDownloadCompleteNotification(
            fileName = fileName
        )
    }
}

class ShowDownloadFailedNotificationUseCase(
    private val repository: NotificationSettingsRepository,
    private val notificationManager: AppNotificationManager
) {
    suspend operator fun invoke(
        reason: String
    ) {
        val settings = repository.observeNotificationSettings().first()

        if (!settings.downloadFailedEnabled) return

        notificationManager.showDownloadFailedNotification(
            reason = reason
        )
    }
}

class ShowAppUpdateNotificationUseCase(
    private val repository: NotificationSettingsRepository,
    private val notificationManager: AppNotificationManager
) {
    suspend operator fun invoke(
        title: String,
        message: String
    ) {
        val settings = repository.observeNotificationSettings().first()

        if (!settings.appUpdatesEnabled) return

        notificationManager.showAppUpdateNotification(
            title = title,
            message = message
        )
    }
}