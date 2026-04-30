package com.app.videodownloader.presentation.screens.main.events

// Intent (User actions)
sealed interface FileDialogIntent {
    data object OnPlayClicked : FileDialogIntent
    data object OnAddToQueueClicked : FileDialogIntent
    data object OnShareClicked : FileDialogIntent
    data object OnRenameClicked : FileDialogIntent
    data object OnMoveClicked : FileDialogIntent
    data object OnInfoClicked : FileDialogIntent
    data object OnDeleteClicked : FileDialogIntent
    data object OnDismiss : FileDialogIntent
}