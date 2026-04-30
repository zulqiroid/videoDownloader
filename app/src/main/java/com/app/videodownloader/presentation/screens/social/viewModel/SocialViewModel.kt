package com.app.videodownloader.presentation.screens.social.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.usecases.GetTrendingReelsUseCase
import com.app.videodownloader.presentation.screens.home.events.HomeEvents
import com.app.videodownloader.presentation.screens.social.events.SocialEvents
import com.app.videodownloader.presentation.screens.social.states.SocialStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SocialViewModel(
    private val getTrendingReels: GetTrendingReelsUseCase

): ViewModel() {

    private val _state = MutableStateFlow(SocialStates())
    val state = _state.asStateFlow()

    fun onEvent(event: SocialEvents) {
        when(event){
            SocialEvents.LoadReels -> {
                loadReels()
            }
            is SocialEvents.OnUrlChange -> {
                _state.update {
                    it.copy(
                        url = event.value
                    )
                }
            }
            SocialEvents.Refresh -> {
                loadReels()
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