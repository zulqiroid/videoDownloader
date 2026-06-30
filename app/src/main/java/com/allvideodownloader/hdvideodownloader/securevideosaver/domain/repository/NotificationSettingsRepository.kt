package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationSettingsRepository {

    fun observeNotificationSettings(): Flow<NotificationSettings>

    suspend fun updateNotificationSettings(settings: NotificationSettings)
}