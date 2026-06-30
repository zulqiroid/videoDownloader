package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.PictureInPicturePlaybackState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.PictureInPictureStateStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultPictureInPictureStateStore : PictureInPictureStateStore {

    private val _state = MutableStateFlow(PictureInPicturePlaybackState())
    override val state: StateFlow<PictureInPicturePlaybackState> = _state.asStateFlow()

    override fun setSupported(
        supported: Boolean
    ) {
        _state.update {
            it.copy(isSupported = supported)
        }
    }

    override fun setInPictureInPictureMode(
        inPictureInPictureMode: Boolean
    ) {
        _state.update {
            it.copy(isInPictureInPictureMode = inPictureInPictureMode)
        }
    }
}