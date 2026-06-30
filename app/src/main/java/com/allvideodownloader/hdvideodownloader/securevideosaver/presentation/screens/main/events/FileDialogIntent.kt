package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.events

import android.net.Uri

// Intent (User actions)
sealed interface FileDialogIntent {
    data object OnPlayClicked: FileDialogIntent
    data object OnAddToQueueClicked : FileDialogIntent
    data object OnShareClicked : FileDialogIntent
    data object OnRenameClicked : FileDialogIntent
    data object OnMoveClicked : FileDialogIntent
    data object OnInfoClicked : FileDialogIntent
    data object OnDeleteClicked : FileDialogIntent
    data object OnDismiss : FileDialogIntent

    data object OnFileInfoDismiss : FileDialogIntent

    data object OnRenameDismiss : FileDialogIntent
    data object OnRenameConfirmClicked : FileDialogIntent

    data class OnRenameValueChanged(
        val value: String
    ) : FileDialogIntent

    data object OnDeleteDismiss : FileDialogIntent
    data object OnDeleteConfirmClicked : FileDialogIntent
    data object OnDeletePermissionGranted : FileDialogIntent
    data object OnDeletePermissionDenied : FileDialogIntent

    data class OnMoveDestinationSelected(
        val destinationTreeUri: Uri
    ) : FileDialogIntent
    data object OnMoveDestinationSelectionCancelled : FileDialogIntent
    data object OnMoveDeletePermissionGranted : FileDialogIntent
    data object OnMoveDeletePermissionDenied : FileDialogIntent
}