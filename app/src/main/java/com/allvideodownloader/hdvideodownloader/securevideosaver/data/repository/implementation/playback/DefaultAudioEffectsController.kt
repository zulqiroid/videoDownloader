package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.playback

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.AudioEffectsState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.AudioEffectsState.Companion.AUDIO_SESSION_UNSET
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.AudioEffectsState.Companion.DEFAULT_BASS_STRENGTH
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.AudioEffectsState.Companion.MAX_BASS_STRENGTH
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.AudioEffectsState.Companion.MIN_BASS_STRENGTH
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.AudioEqualizerBand
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.AudioEqualizerPreset
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.AudioEffectsController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Suppress("TooGenericExceptionCaught")
class DefaultAudioEffectsController : AudioEffectsController {

    private val _state = MutableStateFlow(AudioEffectsState())
    override val state: StateFlow<AudioEffectsState> = _state.asStateFlow()

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null

    private var lastEqualizerEnabled: Boolean = false
    private var lastBassBoostEnabled: Boolean = false
    private var lastBassStrength: Short = DEFAULT_BASS_STRENGTH
    private val lastBandLevels = mutableMapOf<Short, Short>()

    override fun attachToAudioSession(audioSessionId: Int) {
        if (audioSessionId <= AUDIO_SESSION_UNSET) {
            detachFromAudioSession()
            return
        }

        if (_state.value.audioSessionId == audioSessionId && _state.value.isAvailable) return

        releaseEffectsOnly()

        var createdEqualizer: Equalizer? = null
        var createdBassBoost: BassBoost? = null
        var error: String? = null

        try {
            createdEqualizer = Equalizer(EFFECT_PRIORITY, audioSessionId).apply {
                enabled = lastEqualizerEnabled
            }
        } catch (exception: Throwable) {
            error = exception.message ?: "Equalizer is not supported on this device"
        }

        try {
            createdBassBoost = BassBoost(EFFECT_PRIORITY, audioSessionId).apply {
                enabled = lastBassBoostEnabled
                if (strengthSupported) {
                    setStrength(lastBassStrength)
                }
            }
        } catch (exception: Throwable) {
            error = error ?: exception.message ?: "Bass boost is not supported on this device"
        }

        equalizer = createdEqualizer
        bassBoost = createdBassBoost

        restoreBandLevelsIfPossible()
        publishState(
            audioSessionId = audioSessionId,
            errorMessage = error
        )
    }

    override fun setEqualizerEnabled(enabled: Boolean) {
        lastEqualizerEnabled = enabled

        val currentEqualizer = equalizer
        if (currentEqualizer == null) {
            _state.update {
                it.copy(
                    isEqualizerEnabled = false,
                    errorMessage = "Equalizer is not ready yet"
                )
            }
            return
        }

        runSafely(
            onFailureMessage = "Unable to ${if (enabled) "enable" else "disable"} equalizer"
        ) {
            currentEqualizer.enabled = enabled
            publishState()
        }
    }

    override fun setBandLevel(
        bandIndex: Short,
        levelMb: Short
    ) {
        val currentEqualizer = equalizer
        if (currentEqualizer == null) {
            _state.update {
                it.copy(errorMessage = "Equalizer is not ready yet")
            }
            return
        }

        val levelRange = currentEqualizer.bandLevelRange
        val safeLevel = levelMb.coerceIn(
            minimumValue = levelRange[0],
            maximumValue = levelRange[1]
        )

        runSafely(
            onFailureMessage = "Unable to update equalizer band"
        ) {
            currentEqualizer.setBandLevel(
                bandIndex,
                safeLevel
            )

            lastBandLevels[bandIndex] = safeLevel
            publishState()
        }
    }

    override fun applyPreset(presetIndex: Short) {
        val currentEqualizer = equalizer
        if (currentEqualizer == null) {
            _state.update {
                it.copy(errorMessage = "Equalizer is not ready yet")
            }
            return
        }

        val presetCount = currentEqualizer.numberOfPresets
        if (presetIndex !in 0 until presetCount) {
            _state.update {
                it.copy(errorMessage = "Invalid equalizer preset")
            }
            return
        }

        runSafely(
            onFailureMessage = "Unable to apply equalizer preset"
        ) {
            currentEqualizer.usePreset(presetIndex)
            currentEqualizer.enabled = true

            lastEqualizerEnabled = true
            lastBandLevels.clear()

            publishState(
                selectedPresetIndex = presetIndex
            )
        }
    }

    override fun setBassBoostEnabled(enabled: Boolean) {
        lastBassBoostEnabled = enabled

        val currentBassBoost = bassBoost
        if (currentBassBoost == null) {
            _state.update {
                it.copy(
                    isBassBoostEnabled = false,
                    errorMessage = "Bass boost is not ready yet"
                )
            }
            return
        }

        runSafely(
            onFailureMessage = "Unable to ${if (enabled) "enable" else "disable"} bass boost"
        ) {
            currentBassBoost.enabled = enabled
            publishState()
        }
    }

    override fun setBassStrength(strength: Short) {
        val safeStrength = strength.coerceIn(
            minimumValue = MIN_BASS_STRENGTH,
            maximumValue = MAX_BASS_STRENGTH
        )

        lastBassStrength = safeStrength

        val currentBassBoost = bassBoost
        if (currentBassBoost == null) {
            _state.update {
                it.copy(
                    bassStrength = safeStrength,
                    errorMessage = "Bass boost is not ready yet"
                )
            }
            return
        }

        if (!currentBassBoost.strengthSupported) {
            _state.update {
                it.copy(
                    bassStrength = safeStrength,
                    isBassStrengthControlSupported = false,
                    errorMessage = "Bass strength control is not supported on this device"
                )
            }
            return
        }

        runSafely(
            onFailureMessage = "Unable to update bass strength"
        ) {
            currentBassBoost.setStrength(safeStrength)
            publishState()
        }
    }

    override fun detachFromAudioSession() {
        releaseEffectsOnly()

        _state.value = AudioEffectsState(
            isEqualizerEnabled = lastEqualizerEnabled,
            isBassBoostEnabled = lastBassBoostEnabled,
            bassStrength = lastBassStrength
        )
    }

    private fun restoreBandLevelsIfPossible() {
        val currentEqualizer = equalizer ?: return
        if (lastBandLevels.isEmpty()) return

        val levelRange = currentEqualizer.bandLevelRange

        lastBandLevels.forEach { (bandIndex, levelMb) ->
            val safeLevel = levelMb.coerceIn(
                minimumValue = levelRange[0],
                maximumValue = levelRange[1]
            )

            runCatching {
                currentEqualizer.setBandLevel(
                    bandIndex,
                    safeLevel
                )
            }
        }
    }

    private fun publishState(
        audioSessionId: Int = _state.value.audioSessionId,
        selectedPresetIndex: Short? = _state.value.selectedPresetIndex,
        errorMessage: String? = null
    ) {
        val currentEqualizer = equalizer
        val currentBassBoost = bassBoost

        val equalizerBands = currentEqualizer?.readBands().orEmpty()
        val equalizerPresets = currentEqualizer?.readPresets().orEmpty()

        _state.update {
            it.copy(
                audioSessionId = audioSessionId,
                isAvailable = currentEqualizer != null || currentBassBoost != null,

                isEqualizerSupported = currentEqualizer != null,
                isEqualizerEnabled = currentEqualizer?.enabled == true,
                bands = equalizerBands,
                presets = equalizerPresets,
                selectedPresetIndex = selectedPresetIndex,

                isBassBoostSupported = currentBassBoost != null,
                isBassBoostEnabled = currentBassBoost?.enabled == true,
                isBassStrengthControlSupported = currentBassBoost?.strengthSupported == true,
                bassStrength = lastBassStrength,

                errorMessage = errorMessage
            )
        }
    }

    private fun Equalizer.readBands(): List<AudioEqualizerBand> {
        val bandRange = bandLevelRange
        val totalBands = numberOfBands.toInt()

        return List(totalBands) { index ->
            val bandIndex = index.toShort()

            AudioEqualizerBand(
                index = bandIndex,
                centerFrequencyHz = getCenterFreq(bandIndex) / MILL_HZ_IN_HZ,
                minLevelMb = bandRange[0],
                maxLevelMb = bandRange[1],
                levelMb = getBandLevel(bandIndex)
            )
        }
    }

    private fun Equalizer.readPresets(): List<AudioEqualizerPreset> {
        val totalPresets = numberOfPresets.toInt()

        return List(totalPresets) { index ->
            val presetIndex = index.toShort()

            AudioEqualizerPreset(
                index = presetIndex,
                name = getPresetName(presetIndex)
            )
        }
    }

    private fun runSafely(
        onFailureMessage: String,
        block: () -> Unit
    ) {
        try {
            block()
        } catch (exception: Throwable) {
            _state.update {
                it.copy(
                    errorMessage = exception.message ?: onFailureMessage
                )
            }
        }
    }

    private fun releaseEffectsOnly() {
        runCatching {
            equalizer?.enabled = false
            equalizer?.release()
        }

        equalizer = null

        runCatching {
            bassBoost?.enabled = false
            bassBoost?.release()
        }

        bassBoost = null
    }

    private companion object {
        private const val EFFECT_PRIORITY = 0
        private const val MILL_HZ_IN_HZ = 1000
    }
}