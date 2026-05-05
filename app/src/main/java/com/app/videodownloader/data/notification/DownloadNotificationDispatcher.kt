package com.app.videodownloader.data.notification

import com.app.videodownloader.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.flow.first

class DownloadNotificationDispatcher(
    private val notificationSettingsRepository: NotificationSettingsRepository,
    private val appNotificationManager: AppNotificationManager,
) {

    suspend fun notifyDownloadCompleted(
        fileName: String,
    ) {
        val settings = notificationSettingsRepository
            .observeNotificationSettings()
            .first()

        if (!settings.downloadCompleteEnabled) return

        appNotificationManager.showDownloadCompleteNotification(
            fileName = fileName
        )
    }

    suspend fun notifyDownloadFailed(
        reason: String,
    ) {
        val settings = notificationSettingsRepository
            .observeNotificationSettings()
            .first()

        if (!settings.downloadFailedEnabled) return

        appNotificationManager.showDownloadFailedNotification(
            reason = reason
        )
    }
}