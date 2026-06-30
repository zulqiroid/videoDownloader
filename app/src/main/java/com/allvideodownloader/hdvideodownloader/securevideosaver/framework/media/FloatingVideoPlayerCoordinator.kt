package com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.FloatingVideoMiniPlayerState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.AudioEffectsController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FloatingVideoPlayerCoordinator(
    private val applicationScope: CoroutineScope,
    private val audioEffectsController: AudioEffectsController,
){

    private val _state = MutableStateFlow(FloatingVideoMiniPlayerState())
    val state: StateFlow<FloatingVideoMiniPlayerState> = _state.asStateFlow()

    private val _player = MutableStateFlow<Player?>(null)
    val player: StateFlow<Player?> = _player.asStateFlow()

    private var progressJob: Job? = null

    private val videoAudioSessionListener = object : Player.Listener {

        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            attachAudioEffectsToVideoSession(audioSessionId)
        }
    }

    fun attach(
        player: Player,
        mediaList: List<MediaFile>,
        currentIndex: Int,
    ) {
        if (mediaList.isEmpty()) {
            closeAndRelease()
            return
        }

        val safeIndex = currentIndex.coerceIn(
            minimumValue = 0,
            maximumValue = mediaList.lastIndex
        )

        val currentMedia = mediaList[safeIndex]

        if (!currentMedia.isVideo) {
            closeAndRelease()
            return
        }

        attachVideoAudioEffectsListener(player)

        _player.value = player

        _state.value = FloatingVideoMiniPlayerState(
            isVisible = true,
            mediaList = mediaList,
            currentIndex = safeIndex,
            currentMedia = currentMedia,
            isPlaying = player.isPlaying,
            positionMs = player.currentPosition.coerceAtLeast(0L),
            durationMs = player.duration.takeIf { it > 0L } ?: 0L
        )

        startProgressObserver()
    }

    fun consumePlayerForFullPlayer(
        media: MediaFile,
    ): Player? {
        val currentState = _state.value
        val currentMedia = currentState.currentMedia ?: return null

        val isSameVideo =
            currentMedia.id == media.id ||
                    currentMedia.filePath == media.filePath

        if (!isSameVideo) return null

        progressJob?.cancel()
        progressJob = null

        val activePlayer = _player.value

        detachVideoAudioEffectsListener(activePlayer)

        _player.value = null
        _state.value = FloatingVideoMiniPlayerState()

        return activePlayer
    }

    fun togglePlayPause() {
        val activePlayer = _player.value ?: return

        if (activePlayer.isPlaying) {
            activePlayer.pause()
        } else {
            activePlayer.play()
        }

        publishSnapshot(activePlayer)
    }

    fun closeAndRelease() {
        progressJob?.cancel()
        progressJob = null

        val activePlayer = _player.value

        detachVideoAudioEffectsListener(activePlayer)
        audioEffectsController.detachFromAudioSession()

        _player.value = null
        _state.value = FloatingVideoMiniPlayerState()

        runCatching {
            activePlayer?.pause()
            activePlayer?.stop()
            activePlayer?.clearMediaItems()
            activePlayer?.release()
        }
    }

    private fun startProgressObserver() {
        if (progressJob?.isActive == true) return

        progressJob = applicationScope.launch {
            while (isActive) {
                val activePlayer = _player.value

                if (activePlayer != null && _state.value.hasActiveVideo) {
                    publishSnapshot(activePlayer)
                }

                delay(PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun publishSnapshot(
        player: Player,
    ) {
        val currentState = _state.value

        if (!currentState.hasActiveVideo) return

        _state.value = currentState.copy(
            isPlaying = player.isPlaying,
            positionMs = player.currentPosition.coerceAtLeast(0L),
            durationMs = player.duration.takeIf { it > 0L } ?: 0L
        )
    }

    private fun attachVideoAudioEffectsListener(
        player: Player
    ) {
        player.removeListener(videoAudioSessionListener)
        player.addListener(videoAudioSessionListener)

        if (player is ExoPlayer && player.audioSessionId > 0) {
            attachAudioEffectsToVideoSession(player.audioSessionId)
        }
    }

    private fun detachVideoAudioEffectsListener(
        player: Player?
    ) {
        runCatching {
            player?.removeListener(videoAudioSessionListener)
        }
    }

    private fun attachAudioEffectsToVideoSession(
        audioSessionId: Int
    ) {
        if (audioSessionId <= 0) return

        audioEffectsController.attachToAudioSession(audioSessionId)
    }

    fun hideForFullPlayerOpening() {
        val currentState = _state.value

        if (currentState.currentMedia?.isVideo != true) return

        /*
         * Player ko keep karna hai, sirf UI hide karni hai.
         * Is se mini PlayerView composition se remove ho jata hai aur
         * full PlayerView clean surface ke sath attach hota hai.
         */
        _state.value = currentState.copy(
            isVisible = false
        )
    }

    private companion object {
        private const val PROGRESS_UPDATE_INTERVAL_MS = 500L
    }
}