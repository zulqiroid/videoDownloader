package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback

data class AudioEffectsState(
    val audioSessionId: Int = AUDIO_SESSION_UNSET,
    val isAvailable: Boolean = false,

    val isEqualizerSupported: Boolean = false,
    val isEqualizerEnabled: Boolean = false,
    val bands: List<AudioEqualizerBand> = emptyList(),
    val presets: List<AudioEqualizerPreset> = emptyList(),
    val selectedPresetIndex: Short? = null,

    val isBassBoostSupported: Boolean = false,
    val isBassBoostEnabled: Boolean = false,
    val isBassStrengthControlSupported: Boolean = false,
    val bassStrength: Short = DEFAULT_BASS_STRENGTH,

    val errorMessage: String? = null,
) {
    companion object {
        const val AUDIO_SESSION_UNSET = 0
        const val DEFAULT_BASS_STRENGTH: Short = 500
        const val MIN_BASS_STRENGTH: Short = 0
        const val MAX_BASS_STRENGTH: Short = 1000
    }
}

data class AudioEqualizerBand(
    val index: Short,
    val centerFrequencyHz: Int,
    val minLevelMb: Short,
    val maxLevelMb: Short,
    val levelMb: Short,
)

data class AudioEqualizerPreset(
    val index: Short,
    val name: String,
)