package com.app.videodownloader.presentation.screens.player.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.usecases.GetAudiosUseCase
import com.app.videodownloader.domain.usecases.GetVideosUseCase
import com.app.videodownloader.domain.usecases.ads.LoadNativeAdUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdPoolsUseCase
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdSlotHelper
import com.app.videodownloader.presentation.screens.player.states.PlayerState
import com.app.videodownloader.presentation.screens.player.states.PlayerTab
import kotlinx.coroutines.Job
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
    private val observeNativeAdPoolsUseCase: ObserveNativeAdPoolsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state = _state.asStateFlow()

    private var observeMediaJob: Job? = null

    init {
        observeNativeAdPools()
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

                    loadNativeSlotsForCurrentTab()
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

    private fun observeNativeAdPools() {
        viewModelScope.launch {
            observeNativeAdPoolsUseCase().collect { nativeAdPools ->
                _state.update {
                    it.copy(nativeAdPools = nativeAdPools)
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

                loadNativeSlotsForCurrentTab()
            }
        }
    }

    fun onTabChange(tab: PlayerTab) {
        _state.update {
            it.copy(selectedTab = tab)
        }

        loadNativeSlotsForCurrentTab()
    }

    private fun loadNativeSlotsForCurrentTab() {
        val currentState = _state.value
        val placementKey = NativeAdConfig.PLAYER_LIST
        val placementConfig = currentState.nativeAdConfig.placement(placementKey) ?: return

        val totalItems = when (currentState.selectedTab) {
            PlayerTab.VIDEO -> currentState.videos.size
            PlayerTab.AUDIO -> currentState.audios.size
        }

        if (totalItems <= 0) return

        val slotKeys = NativeAdSlotHelper.insertionSlotKeys(
            totalItems = totalItems,
            config = placementConfig
        )

        slotKeys.forEach { slotKey ->
            loadNativeAdUseCase(
                placementKey = placementKey,
                slotKey = slotKey,
                onStateChanged = { adState ->
                    Log.d(
                        TAG,
                        "Player native ad state. placement=$placementKey slot=$slotKey state=$adState"
                    )
                }
            )
        }
    }

    companion object {
        private const val TAG = "PlayerViewModel"
    }
}