package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.AudioEffectsController

class ObserveAudioEffectsStateUseCase(
    private val audioEffectsController: AudioEffectsController
) {
    operator fun invoke() = audioEffectsController.state
}