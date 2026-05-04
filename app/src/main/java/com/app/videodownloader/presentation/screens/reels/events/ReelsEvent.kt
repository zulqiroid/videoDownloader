package com.app.videodownloader.presentation.screens.reels.events


sealed class ReelsEvent {
    object LoadReels : ReelsEvent()

    data class OnPageChanged(
        val index: Int
    ) : ReelsEvent()

    data class OnLikeClicked(
        val reelId: String
    ) : ReelsEvent()
}