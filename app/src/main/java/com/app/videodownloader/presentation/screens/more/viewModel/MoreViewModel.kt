package com.app.videodownloader.presentation.screens.more.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.usecases.ads.LoadNativeAdUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdsUseCase
import com.app.videodownloader.presentation.screens.more.events.MoreUiEvent
import com.app.videodownloader.presentation.screens.more.states.MoreState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoreViewModel(
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    init {
        observeNativeAds()
        observeNativeAdConfig()
    }

    fun onEvent(event: MoreUiEvent) {
        when (event) {
            MoreUiEvent.ScreenStarted -> {
                loadVisibleNativeAds()
            }
        }
    }

    private fun observeNativeAds() {
        viewModelScope.launch {
            observeNativeAdsUseCase().collect { nativeAds ->
                _state.update {
                    it.copy(nativeAds = nativeAds)
                }
            }
        }
    }

    private fun observeNativeAdConfig() {
        viewModelScope.launch {
            observeNativeAdConfigUseCase().collect { config ->
                _state.update {
                    it.copy(nativeAdConfig = config)
                }

                loadVisibleNativeAds()
            }
        }
    }

    private fun loadVisibleNativeAds() {
        loadIfEnabled(NativeAdConfig.MORE_TOP)
        loadIfEnabled(NativeAdConfig.MORE_BOTTOM)
    }

    private fun loadIfEnabled(
        placementKey: String
    ) {
        val config = _state.value.nativeAdConfig

        if (config.placement(placementKey) == null) {
            return
        }

        loadNativeAdUseCase(
            placementKey = placementKey,
            onStateChanged = { state ->
                Log.d(TAG, "More native ad state. placement=$placementKey state=$state")

                if (state is AdState.LoadFailed) {
                    Log.d(TAG, "More native ad failed. placement=$placementKey")
                }
            }
        )
    }

    companion object {
        private const val TAG = "MoreViewModel"
    }
}