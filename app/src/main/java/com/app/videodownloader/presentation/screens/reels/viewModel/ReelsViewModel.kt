package com.app.videodownloader.presentation.screens.reels.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.usecases.GetTrendingReelsUseCase
import com.app.videodownloader.presentation.screens.reels.events.ReelsEvent
import com.app.videodownloader.presentation.screens.reels.states.ReelUi
import com.app.videodownloader.presentation.screens.reels.states.ReelsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReelsViewModel(
    private val getReelsUseCase: GetTrendingReelsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReelsState())
    val state = _state.asStateFlow()

    fun onEvent(event: ReelsEvent) {
        when (event) {
            ReelsEvent.LoadReels -> loadReels()
            is ReelsEvent.OnPageChanged -> {
                _state.update { it.copy(currentIndex = event.index) }
            }
        }
    }

    private fun loadReels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val categories = getReelsUseCase()

            val reels = categories
                .flatMap { it.reels }
                .map {
                    ReelUi(
                        id = it.id,
                        videoUrl = it.videoUrl,
                        username = "User", // replace with API later
                        caption = ""
                    )
                }

            _state.update {
                it.copy(
                    reels = reels,
                    isLoading = false
                )
            }
        }
    }
}