package com.app.videodownloader.presentation.screens.more.events

sealed interface MoreUiEvent {
    data object ScreenStarted : MoreUiEvent
}