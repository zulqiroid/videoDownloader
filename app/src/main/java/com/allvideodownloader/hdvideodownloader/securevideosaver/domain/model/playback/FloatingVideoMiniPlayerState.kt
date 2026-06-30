package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile

data class FloatingVideoMiniPlayerState(
    val isVisible: Boolean = false,
    val mediaList: List<MediaFile> = emptyList(),
    val currentIndex: Int = 0,
    val currentMedia: MediaFile? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
) {
    val hasActiveVideo: Boolean
        get() = isVisible && currentMedia?.isVideo == true
}