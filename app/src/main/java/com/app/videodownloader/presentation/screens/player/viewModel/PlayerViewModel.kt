package com.app.videodownloader.presentation.screens.player.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.usecases.GetAudiosUseCase
import com.app.videodownloader.domain.usecases.GetVideosUseCase
import com.app.videodownloader.domain.usecases.ads.LoadNativeAdUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdsUseCase
import com.app.videodownloader.presentation.screens.player.states.PlayerState
import com.app.videodownloader.presentation.screens.player.states.PlayerTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val getVideos: GetVideosUseCase,
    private val getAudios: GetAudiosUseCase,
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state = _state.asStateFlow()

    init {
        observeMedia()
        observeNativeAds()
        observeNativeAdConfig()
    }

    private fun observeMedia() {
        viewModelScope.launch {
            combine(
                getVideos(),
                getAudios()
            ) { videos, audios ->
                videos to audios
            }
                .onStart {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }
                .collect { (videos, audios) ->
                    _state.update {
                        it.copy(
                            videos = videos,
                            audios = audios,
                            isLoading = false
                        )
                    }
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

                if (config.placement(NativeAdConfig.PLAYER_LIST) != null) {
                    loadPlayerListNativeAd()
                }
            }
        }
    }

    private fun loadPlayerListNativeAd() {
        loadNativeAdUseCase(
            placementKey = NativeAdConfig.PLAYER_LIST,
            onStateChanged = { adState ->
                Log.d(TAG, "Player list native ad state: $adState")
            }
        )
    }

    fun onTabChange(tab: PlayerTab) {
        _state.update {
            it.copy(selectedTab = tab)
        }
    }

    companion object {
        private const val TAG = "PlayerViewModel"
    }
}