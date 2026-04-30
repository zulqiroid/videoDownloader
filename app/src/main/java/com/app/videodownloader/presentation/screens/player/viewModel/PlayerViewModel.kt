package com.app.videodownloader.presentation.screens.player.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.usecases.GetAudiosUseCase
import com.app.videodownloader.domain.usecases.GetVideosUseCase
import com.app.videodownloader.presentation.screens.main.events.FileDialogIntent
 import com.app.videodownloader.presentation.screens.player.states.PlayerState
import com.app.videodownloader.presentation.screens.player.states.PlayerTab
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val getVideos: GetVideosUseCase,
    private val getAudios: GetAudiosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state = _state.asStateFlow()

    init {
        loadMedia()
    }

    private fun loadMedia() {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true) }

            val videos = getVideos()
            val audios = getAudios()

            _state.update {
                it.copy(
                    videos = videos,
                    audios = audios,
                    isLoading = false
                )
            }
        }
    }

    fun onTabChange(tab: PlayerTab) {
        _state.update { it.copy(selectedTab = tab) }
    }


}