package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.MediaPlaybackState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.PlaybackMediaType
import kotlinx.coroutines.flow.StateFlow

interface MediaPlaybackStateStore {

    val state: StateFlow<MediaPlaybackState>

    fun setActiveMedia(
        mediaList: List<MediaFile>,
        currentIndex: Int,
        mediaType: PlaybackMediaType,
        isPlaying: Boolean
    )

    fun updatePlayerState(
        isPlaying: Boolean,
        isBuffering: Boolean,
        positionMs: Long,
        durationMs: Long,
        playbackSpeed: Float,
        volume: Float,
        isMuted: Boolean
    )

    fun updatePosition(
        positionMs: Long,
        durationMs: Long,
        isPlaying: Boolean
    )

    fun updateVolume(
        volume: Float,
        isMuted: Boolean
    )

    fun updatePlaybackSpeed(
        playbackSpeed: Float
    )

    fun clear()
}