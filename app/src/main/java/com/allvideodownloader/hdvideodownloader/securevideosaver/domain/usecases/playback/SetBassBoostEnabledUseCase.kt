package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.AudioEffectsController

class SetBassBoostEnabledUseCase(
    private val audioEffectsController: AudioEffectsController
) {
    operator fun invoke(enabled: Boolean) {
        audioEffectsController.setBassBoostEnabled(enabled)
    }
}