package com.app.videodownloader.presentation.ads.banner.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.usecases.ads.ObserveBannerAdConfigUseCase
 import com.app.videodownloader.presentation.ads.banner.states.BannerAdState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BannerAdViewModel(
    private val observeBannerAdConfigUseCase: ObserveBannerAdConfigUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BannerAdState())
    val state = _state.asStateFlow()

    init {
        observeBannerConfig()
    }

    private fun observeBannerConfig() {
        viewModelScope.launch {
            observeBannerAdConfigUseCase()
                .collect { config ->
                    _state.update {
                        it.copy(config = config)
                    }
                }
        }
    }
}