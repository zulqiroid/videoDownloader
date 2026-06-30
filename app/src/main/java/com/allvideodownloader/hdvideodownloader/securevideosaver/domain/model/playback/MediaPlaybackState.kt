package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile

data class MediaPlaybackState(
    val mediaList: List<MediaFile> = emptyList(),
    val currentIndex: Int = 0,
    val currentMedia: MediaFile? = null,
    val mediaType: PlaybackMediaType = PlaybackMediaType.None,

    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,

    val positionMs: Long = 0L,
    val durationMs: Long = 0L,

    val playbackSpeed: Float = DEFAULT_PLAYBACK_SPEED,
    val volume: Float = DEFAULT_VOLUME,
    val isMuted: Boolean = false,

    val errorMessage: String? = null,
) {
    val hasActiveMedia: Boolean
        get() = currentMedia != null

    val isAudio: Boolean
        get() = mediaType == PlaybackMediaType.Audio

    val isVideo: Boolean
        get() = mediaType == PlaybackMediaType.Video

    val canShowAudioMiniPlayer: Boolean
        get() = hasActiveMedia && mediaType == PlaybackMediaType.Audio

    val canShowVideoMiniPlayer: Boolean
        get() = hasActiveMedia && mediaType == PlaybackMediaType.Video

    companion object {
        const val DEFAULT_PLAYBACK_SPEED = 1f
        const val DEFAULT_VOLUME = 1f
    }
}

enum class PlaybackMediaType {
    None,
    Audio,
    Video
}