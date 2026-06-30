package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.MediaPlaybackState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.PlaybackMediaType
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.MediaPlaybackStateStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultMediaPlaybackStateStore : MediaPlaybackStateStore {

    private val _state = MutableStateFlow(MediaPlaybackState())
    override val state: StateFlow<MediaPlaybackState> = _state.asStateFlow()

    override fun setActiveMedia(
        mediaList: List<MediaFile>,
        currentIndex: Int,
        mediaType: PlaybackMediaType,
        isPlaying: Boolean
    ) {
        if (mediaList.isEmpty()) {
            clear()
            return
        }

        val safeIndex = currentIndex.coerceIn(
            minimumValue = 0,
            maximumValue = mediaList.lastIndex
        )

        val selectedMedia = mediaList[safeIndex]

        _state.update { current ->
            current.copy(
                mediaList = mediaList,
                currentIndex = safeIndex,
                currentMedia = selectedMedia,
                mediaType = mediaType,
                isPlaying = isPlaying,
                isBuffering = true,
                positionMs = 0L,
                durationMs = 0L,
                errorMessage = null
            )
        }
    }

    override fun updatePlayerState(
        isPlaying: Boolean,
        isBuffering: Boolean,
        positionMs: Long,
        durationMs: Long,
        playbackSpeed: Float,
        volume: Float,
        isMuted: Boolean
    ) {
        val safeDuration = durationMs.coerceAtLeast(0L)
        val safePosition = positionMs
            .coerceAtLeast(0L)
            .coerceAtMost(
                safeDuration.takeIf { it > 0L } ?: positionMs.coerceAtLeast(0L)
            )

        val safeVolume = volume.coerceIn(0f, 1f)

        _state.update { current ->
            current.copy(
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                positionMs = safePosition,
                durationMs = safeDuration,
                playbackSpeed = playbackSpeed.coerceIn(0.25f, 4f),
                volume = safeVolume,
                isMuted = isMuted || safeVolume == 0f,
                errorMessage = null
            )
        }
    }

    override fun updatePosition(
        positionMs: Long,
        durationMs: Long,
        isPlaying: Boolean
    ) {
        val safeDuration = durationMs.coerceAtLeast(0L)
        val safePosition = positionMs
            .coerceAtLeast(0L)
            .coerceAtMost(
                safeDuration.takeIf { it > 0L } ?: positionMs.coerceAtLeast(0L)
            )

        _state.update { current ->
            current.copy(
                positionMs = safePosition,
                durationMs = safeDuration,
                isPlaying = isPlaying
            )
        }
    }

    override fun updateVolume(
        volume: Float,
        isMuted: Boolean
    ) {
        val safeVolume = volume.coerceIn(0f, 1f)

        _state.update { current ->
            current.copy(
                volume = safeVolume,
                isMuted = isMuted || safeVolume == 0f
            )
        }
    }

    override fun updatePlaybackSpeed(
        playbackSpeed: Float
    ) {
        _state.update { current ->
            current.copy(
                playbackSpeed = playbackSpeed.coerceIn(0.25f, 4f)
            )
        }
    }

    override fun clear() {
        _state.value = MediaPlaybackState()
    }
}