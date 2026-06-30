package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetAudiosUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetVideosUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadNativeAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdPoolsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdSlotHelper
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states.PlayerState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states.PlayerTab
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states.currentTabItems
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.viewModel.ReelsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val getVideos: GetVideosUseCase,
    private val getAudios: GetAudiosUseCase,
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state = _state.asStateFlow()

    private var observeMediaJob: Job? = null

    init {
        observePremiumStatus()
        observeNativeAds()
        observeNativeAdConfig()
    }

    fun onMediaPermissionGranted() {
        if (observeMediaJob?.isActive == true) return

        observeMediaJob = viewModelScope.launch {
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

    fun onMediaPermissionDenied() {
        observeMediaJob?.cancel()
        observeMediaJob = null

        _state.update {
            it.copy(
                videos = emptyList(),
                audios = emptyList(),
                isLoading = false
            )
        }
    }

    fun onTabChange(tab: PlayerTab) {
        _state.update {
            it.copy(
                selectedTab = tab
            )
        }

     }


    private fun observePremiumStatus() {
        viewModelScope.launch {
            observeIsPremiumUserUseCase()
                .distinctUntilChanged()
                .collect { isPremium ->
                    _state.update { currentState ->
                        currentState.copy(
                            isPremiumUser = isPremium,
                            nativeAds = if (isPremium) emptyMap() else currentState.nativeAds
                        )
                    }

                    if (!isPremium) {
                        loadVisibleNativeAds()
                    } else {
                        Log.d(TAG, "More native ads skipped: premium user")
                    }
                }
        }
    }

    private fun observeNativeAds() {
        viewModelScope.launch {
            observeNativeAdsUseCase().collect { nativeAds ->
                _state.update { currentState ->
                    if (currentState.isPremiumUser) {
                        currentState.copy(nativeAds = emptyMap())
                    } else {
                        currentState.copy(nativeAds = nativeAds)
                    }
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
        if (_state.value.isPremiumUser) {
            Log.d(TAG, "More native ad load skipped: premium user")
            return
        }

        loadIfEnabled(NativeAdConfig.PLAYER_LIST)
    }

    private fun loadIfEnabled(
        placementKey: String
    ) {
        val currentState = _state.value

        if (currentState.isPremiumUser) {
            Log.d(TAG, "More native ad skipped: premium user. placement=$placementKey")
            return
        }

        val config = currentState.nativeAdConfig

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
        private const val TAG = "PlayerViewModel"
        private const val MAX_SEARCH_QUERY_LENGTH = 80
    }
}