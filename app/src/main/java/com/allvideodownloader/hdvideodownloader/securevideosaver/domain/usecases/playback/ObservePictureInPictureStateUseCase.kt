package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.PictureInPictureStateStore

class ObservePictureInPictureStateUseCase(
    private val pictureInPictureStateStore: PictureInPictureStateStore
) {
    operator fun invoke() = pictureInPictureStateStore.state
}