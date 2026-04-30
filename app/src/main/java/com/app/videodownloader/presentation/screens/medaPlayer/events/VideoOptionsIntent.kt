package com.app.videodownloader.presentation.screens.medaPlayer.events

sealed interface VideoOptionsIntent {
    data object OnPlaybackSpeedClicked : VideoOptionsIntent
    data object OnFileInfoClicked : VideoOptionsIntent
    data object OnShareClicked : VideoOptionsIntent
    data object OnRenameClicked : VideoOptionsIntent
    data object OnDeleteClicked : VideoOptionsIntent
    data object OnDismiss : VideoOptionsIntent
}