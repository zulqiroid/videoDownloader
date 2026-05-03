package com.app.videodownloader.domain.repository

import com.app.videodownloader.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationSettingsRepository {

    fun observeNotificationSettings(): Flow<NotificationSettings>

    suspend fun updateNotificationSettings(settings: NotificationSettings)
}