package com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.notification

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationSettingsLocalDataSource {

    fun observeNotificationSettings(): Flow<NotificationSettings>

    suspend fun updateNotificationSettings(settings: NotificationSettings)
}