package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.AudioEffectsController

class SetBassBoostStrengthUseCase(
    private val audioEffectsController: AudioEffectsController
) {
    operator fun invoke(strength: Short) {
        audioEffectsController.setBassStrength(strength)
    }
}