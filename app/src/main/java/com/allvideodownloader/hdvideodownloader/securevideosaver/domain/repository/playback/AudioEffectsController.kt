package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.AudioEffectsState
import kotlinx.coroutines.flow.StateFlow

interface AudioEffectsController {

    val state: StateFlow<AudioEffectsState>

    fun attachToAudioSession(audioSessionId: Int)

    fun setEqualizerEnabled(enabled: Boolean)

    fun setBandLevel(
        bandIndex: Short,
        levelMb: Short
    )

    fun applyPreset(presetIndex: Short)

    fun setBassBoostEnabled(enabled: Boolean)

    fun setBassStrength(strength: Short)

    fun detachFromAudioSession()
}