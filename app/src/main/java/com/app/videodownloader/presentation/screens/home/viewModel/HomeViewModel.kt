package com.app.videodownloader.presentation.screens.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.usecases.GetTrendingReelsUseCase
import com.app.videodownloader.presentation.screens.home.events.HomeEvents
import com.app.videodownloader.presentation.screens.home.events.HomeNavEvents
import com.app.videodownloader.presentation.screens.home.states.HomeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTrendingReels: GetTrendingReelsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<HomeNavEvents>()
    val effect = _navEvents.asSharedFlow()

    fun onEvent(event: HomeEvents) {
        when (event) {

            HomeEvents.LoadReels -> {
                loadReels()
            }
            HomeEvents.Refresh -> {
                loadReels()
            }

            is HomeEvents.OnUrlChange -> {
                _state.update {
                    it.copy(
                        url = event.value
                    )
                }
            }

        }
    }

    private fun loadReels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            runCatching {
                getTrendingReels()
            }.onSuccess { categories ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        category = categories
                    )
                }
            }.onFailure {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = ""
                    )
                }
            }
        }
    }

}