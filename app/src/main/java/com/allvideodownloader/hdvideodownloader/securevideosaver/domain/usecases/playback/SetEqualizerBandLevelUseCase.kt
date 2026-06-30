package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.AudioEffectsController

class SetEqualizerBandLevelUseCase(
    private val audioEffectsController: AudioEffectsController
) {
    operator fun invoke(
        bandIndex: Short,
        levelMb: Short
    ) {
        audioEffectsController.setBandLevel(
            bandIndex = bandIndex,
            levelMb = levelMb
        )
    }
}