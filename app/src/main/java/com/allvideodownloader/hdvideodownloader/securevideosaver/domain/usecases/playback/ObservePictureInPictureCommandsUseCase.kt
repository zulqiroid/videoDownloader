package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.PictureInPictureCommandBus

class ObservePictureInPictureCommandsUseCase(
    private val pictureInPictureCommandBus: PictureInPictureCommandBus
) {
    operator fun invoke() = pictureInPictureCommandBus.commands
}