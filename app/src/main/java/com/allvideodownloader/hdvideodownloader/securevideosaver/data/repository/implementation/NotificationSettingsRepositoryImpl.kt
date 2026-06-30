package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation

import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.notification.NotificationSettingsLocalDataSource
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.NotificationSettings
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.NotificationSettingsRepository
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