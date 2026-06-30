package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback

interface MediaPlaybackController {

    fun togglePlayPause()

    fun skipToPrevious()

    fun skipToNext()

    fun stopAndClear()
}