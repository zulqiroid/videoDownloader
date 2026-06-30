package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.AudioEffectsController

class ApplyEqualizerPresetUseCase(
    private val audioEffectsController: AudioEffectsController
) {
    operator fun invoke(presetIndex: Short) {
        audioEffectsController.applyPreset(presetIndex)
    }
}