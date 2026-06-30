package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.events

sealed interface NotificationEvents {

    data object OnNotificationClicked : NotificationEvents

    data object OnNotificationDialogDismissed : NotificationEvents

    data object OnNotificationSaveClicked : NotificationEvents

    data class OnDraftDownloadCompleteNotificationChanged(
        val enabled: Boolean
    ) : NotificationEvents

    data class OnDraftDownloadFailedNotificationChanged(
        val enabled: Boolean
    ) : NotificationEvents

    data class OnDraftAppUpdatesNotificationChanged(
        val enabled: Boolean
    ) : NotificationEvents
}