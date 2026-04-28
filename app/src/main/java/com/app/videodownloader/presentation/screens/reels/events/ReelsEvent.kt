package com.app.videodownloader.presentation.screens.reels.events

sealed class ReelsEvent {
    data object LoadReels : ReelsEvent()
    data class OnPageChanged(val index: Int) : ReelsEvent()
}