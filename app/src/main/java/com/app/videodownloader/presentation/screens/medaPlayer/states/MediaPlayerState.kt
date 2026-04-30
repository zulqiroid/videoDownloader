package com.app.videodownloader.presentation.screens.medaPlayer.states


import com.app.videodownloader.domain.model.MediaFile

data class MediaPlayerState(
    val mediaList: List<MediaFile> = emptyList(),
    val currentIndex: Int = 0,

    val isPlaying: Boolean = true,
    val position: Long = 0L,
    val duration: Long = 0L,

    val volume: Float = 1f,
    val isMuted: Boolean = false,

    val isLoading: Boolean = false,

    val showBottomSheet: Boolean = false,
)