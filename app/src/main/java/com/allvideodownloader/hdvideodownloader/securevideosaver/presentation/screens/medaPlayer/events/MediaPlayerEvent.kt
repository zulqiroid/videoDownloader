package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.events

import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.states.VideoGestureControlType


sealed class MediaPlayerEvent {

    data class Load(
        val mediaList: List<com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile>,
        val startIndex: Int
    ) : MediaPlayerEvent()

    data class OnPageChanged(val index: Int) : MediaPlayerEvent()

    data object OnPlayPauseClicked : MediaPlayerEvent()

    data object OnNextClicked : MediaPlayerEvent()

    data object OnPreviousClicked : MediaPlayerEvent()

    data object OnForwardClicked : MediaPlayerEvent()

    data object OnRewindClicked : MediaPlayerEvent()

    data class OnSeek(val position: Long) : MediaPlayerEvent()

    data class OnVolumeChanged(val volume: Float) : MediaPlayerEvent()

    data object OnMuteToggleClicked : MediaPlayerEvent()

    object OnBackPressed : MediaPlayerEvent()

    object OnThreeDotsClick: MediaPlayerEvent()

    data object OnShuffleClicked : MediaPlayerEvent()

    data object OnAudioRepeatClicked : MediaPlayerEvent()

    data object OnPictureInPictureClicked : MediaPlayerEvent()

    data class OnVideoGestureFeedbackChanged(
        val type: VideoGestureControlType,
        val progress: Float,
        val label: String
    ) : MediaPlayerEvent()

 }