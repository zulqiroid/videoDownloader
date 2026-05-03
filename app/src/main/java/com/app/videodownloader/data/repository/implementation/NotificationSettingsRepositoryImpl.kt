package com.app.videodownloader.data.repository.implementation

import com.app.videodownloader.data.local.dataStore.notification.NotificationSettingsLocalDataSource
 import com.app.videodownloader.domain.model.NotificationSettings
import com.app.videodownloader.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.flow.Flow

class NotificationSettingsRepositoryImpl(
    private val localDataSource: NotificationSettingsLocalDataSource
) : NotificationSettingsRepository {

    override fun observeNotificationSettings(): Flow<NotificationSettings> {
        return localDataSource.observeNotificationSettings()
    }

    override suspend fun updateNotificationSettings(settings: NotificationSettings) {
        localDataSource.updateNotificationSettings(settings)
    }
}