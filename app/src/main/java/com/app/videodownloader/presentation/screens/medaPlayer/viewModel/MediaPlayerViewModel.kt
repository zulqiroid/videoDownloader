package com.app.videodownloader.presentation.screens.medaPlayer.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.app.videodownloader.presentation.screens.medaPlayer.events.VideoOptionsIntent
import com.app.videodownloader.presentation.screens.medaPlayer.player.MediaPlayerManager
import com.app.videodownloader.presentation.screens.medaPlayer.states.MediaPlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MediaPlayerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var playerManager = MediaPlayerManager(application)

    private val _state = MutableStateFlow(MediaPlayerState())
    val state = _state.asStateFlow()

    private var progressJob: Job? = null
    private var isLoaded = false

    val player: ExoPlayer
        get() = playerManager.player

    init {
        observeProgress()
    }

    fun onEvent(event: MediaPlayerEvent) {
        when (event) {

            is MediaPlayerEvent.Load -> {
                load(
                    mediaList = event.mediaList,
                    startIndex = event.startIndex
                )
            }

            is MediaPlayerEvent.OnPageChanged -> {
                playIndex(event.index)
            }

            MediaPlayerEvent.OnPlayPauseClicked -> {
                togglePlayPause()
            }

            MediaPlayerEvent.OnNextClicked -> {
                playNext()
            }

            MediaPlayerEvent.OnPreviousClicked -> {
                playPrevious()
            }

            MediaPlayerEvent.OnForwardClicked -> {
                playerManager.forward()
            }

            MediaPlayerEvent.OnRewindClicked -> {
                playerManager.rewind()
            }

            is MediaPlayerEvent.OnSeek -> {
                playerManager.seekTo(event.position)
                _state.update {
                    it.copy(position = event.position)
                }
            }

            is MediaPlayerEvent.OnVolumeChanged -> {
                playerManager.setVolume(event.volume)
                _state.update {
                    it.copy(
                        volume = event.volume,
                        isMuted = event.volume == 0f
                    )
                }
            }

            MediaPlayerEvent.OnMuteToggleClicked -> {
                toggleMute()
            }
            MediaPlayerEvent.OnBackPressed -> {
                resetPlayer()
            }

            MediaPlayerEvent.OnThreeDotsClick -> {
                _state.update {
                    it.copy(
                        showBottomSheet = true
                    )
                }
            }


        }
    }

    fun onBottomSheetIntent (event :VideoOptionsIntent){
        when(event){
            VideoOptionsIntent.OnDeleteClicked -> {

            }
            VideoOptionsIntent.OnDismiss -> {
                _state.update {
                    it.copy(
                        showBottomSheet = false
                    )
                }
            }
            VideoOptionsIntent.OnFileInfoClicked -> {

            }
            VideoOptionsIntent.OnPlaybackSpeedClicked -> {

            }
            VideoOptionsIntent.OnRenameClicked -> {

            }
            VideoOptionsIntent.OnShareClicked -> {

            }
        }
    }

    private fun load(
        mediaList: List<MediaFile>,
        startIndex: Int
    ) {
        if (mediaList.isEmpty()) return

        val safeIndex = startIndex.coerceIn(0, mediaList.lastIndex)

        // 🔥 HARD RESET PLAYER FIRST
        playerManager.reset() // we will add this

        isLoaded = true

        _state.update {
            it.copy(
                mediaList = mediaList,
                currentIndex = safeIndex,
                isPlaying = true,
                isLoading = false,
                position = 0L,
                duration = 0L
            )
        }

//        playerManager.play(mediaList[safeIndex])
    }

    private fun playIndex(index: Int) {
        val current = _state.value
        if (current.mediaList.isEmpty()) return
        if (index !in current.mediaList.indices) return
//        if (index == current.currentIndex) return

        _state.update {
            it.copy(
                currentIndex = index,
                isPlaying = true
            )
        }

        playerManager.play(current.mediaList[index])
    }

    private fun playNext() {
        val current = _state.value
        if (current.mediaList.isEmpty()) return

        val nextIndex = (current.currentIndex + 1).coerceAtMost(current.mediaList.lastIndex)
        playIndex(nextIndex)
    }

    private fun playPrevious() {
        val current = _state.value
        if (current.mediaList.isEmpty()) return

        val previousIndex = (current.currentIndex - 1).coerceAtLeast(0)
        playIndex(previousIndex)
    }

    private fun togglePlayPause() {
        val currentlyPlaying = _state.value.isPlaying

        if (currentlyPlaying) {
            playerManager.pause()
        } else {
            playerManager.play()
        }

        _state.update {
            it.copy(isPlaying = !currentlyPlaying)
        }
    }

    private fun toggleMute() {
        val state = _state.value

        if (state.isMuted) {
            playerManager.setVolume(state.volume.takeIf { it > 0f } ?: 1f)
            _state.update {
                it.copy(isMuted = false)
            }
        } else {
            playerManager.setVolume(0f)
            _state.update {
                it.copy(isMuted = true)
            }
        }
    }

    private fun observeProgress() {
        progressJob?.cancel()

        progressJob = viewModelScope.launch {
            while (true) {
                val duration = player.duration.takeIf { it > 0 } ?: 0L
                val position = player.currentPosition.coerceAtLeast(0L)

                _state.update {
                    it.copy(
                        position = position,
                        duration = duration,
                        isPlaying = player.isPlaying
                    )
                }

                delay(300)
            }
        }
    }

    fun resetPlayer() {
        progressJob?.cancel()

        playerManager.pause()
        playerManager.seekTo(0)
        playerManager.release()

        playerManager = MediaPlayerManager(getApplication())

        isLoaded = false

        _state.value = MediaPlayerState()
    }

    override fun onCleared() {
        progressJob?.cancel()
        playerManager.release()
        super.onCleared()
    }
}