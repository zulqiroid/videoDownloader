package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.PictureInPictureCommand
import kotlinx.coroutines.flow.SharedFlow

interface PictureInPictureCommandBus {

    val commands: SharedFlow<PictureInPictureCommand>

    fun dispatch(command: PictureInPictureCommand)
}