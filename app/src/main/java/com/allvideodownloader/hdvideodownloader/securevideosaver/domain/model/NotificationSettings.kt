package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model

data class NotificationSettings(
    val downloadCompleteEnabled: Boolean = true,
    val downloadFailedEnabled: Boolean = true,
    val appUpdatesEnabled: Boolean = false,
)