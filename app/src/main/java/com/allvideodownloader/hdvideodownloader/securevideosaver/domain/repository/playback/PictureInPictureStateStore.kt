package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.PictureInPicturePlaybackState
import kotlinx.coroutines.flow.StateFlow

interface PictureInPictureStateStore {

    val state: StateFlow<PictureInPicturePlaybackState>

    fun setSupported(
        supported: Boolean
    )

    fun setInPictureInPictureMode(
        inPictureInPictureMode: Boolean
    )
}