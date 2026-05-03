package com.app.videodownloader.data.local.dataStore.notification

import com.app.videodownloader.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationSettingsLocalDataSource {

    fun observeNotificationSettings(): Flow<NotificationSettings>

    suspend fun updateNotificationSettings(settings: NotificationSettings)
}