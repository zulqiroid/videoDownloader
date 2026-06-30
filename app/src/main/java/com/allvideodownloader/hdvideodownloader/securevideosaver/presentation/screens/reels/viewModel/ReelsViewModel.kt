package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.Reel
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetTrendingReelsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadNativeAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.events.ReelsEvent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.states.ReelUi
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.states.ReelsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReelsViewModel(
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
    private val getReelsUseCase: GetTrendingReelsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReelsState())
    val state = _state.asStateFlow()

    init {
        observePremiumStatus()
        observeNativeAds()
        observeNativeAdConfig()
    }

    fun onEvent(event: ReelsEvent) {
        when (event) {
            ReelsEvent.LoadReels -> {
                loadReels()
            }

            is ReelsEvent.OnPageChanged -> {
                onPageChanged(event.index)
            }

            is ReelsEvent.OnLikeClicked -> {
                onLikeClicked(event.reelId)
            }
        }
    }

    private fun onPageChanged(index: Int) {
        _state.update { currentState ->
            currentState.copy(
                currentIndex = index.coerceIn(
                    minimumValue = 0,
                    maximumValue = (currentState.reels.size - 1).coerceAtLeast(0)
                )
            )
        }
    }

    private fun loadReels() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true)
            }

            val categories = getReelsUseCase()

            val likedIds = _state.value.likedReelIds

            val reels = categories
                .flatMap { category -> category.reels }
                .distinctBy { reel -> reel.id }
                .map { reel ->
                    val isLiked = likedIds.contains(reel.id)

                    ReelUi(
                        id = reel.id,
                        videoUrl = reel.videoUrl,
                        username = "User",
                        caption = "",
                        isLiked = isLiked,
                        likeCount = if (isLiked) DEFAULT_LIKE_COUNT + 1 else DEFAULT_LIKE_COUNT
                    )
                }

            _state.update { currentState ->
                currentState.copy(
                    reels = reels,
                    isLoading = false,
                    currentIndex = currentState.currentIndex.coerceIn(
                        minimumValue = 0,
                        maximumValue = (reels.size - 1).coerceAtLeast(0)
                    )
                )
            }
        }
    }

    private fun onLikeClicked(reelId: String) {
        _state.update { currentState ->
            val wasLiked = currentState.likedReelIds.contains(reelId)

            val updatedLikedIds = if (wasLiked) {
                currentState.likedReelIds - reelId
            } else {
                currentState.likedReelIds + reelId
            }

            val updatedReels = currentState.reels.map { reel ->
                if (reel.id != reelId) {
                    reel
                } else {
                    reel.copy(
                        isLiked = !wasLiked,
                        likeCount = if (wasLiked) {
                            (reel.likeCount - 1).coerceAtLeast(0)
                        } else {
                            reel.likeCount + 1
                        }
                    )
                }
            }

            currentState.copy(
                likedReelIds = updatedLikedIds,
                reels = updatedReels
            )
        }
    }

    fun setCurrentIndex(selectedReel: Reel?) {
        if (selectedReel == null) return

        val index = _state.value.reels.indexOfFirst { reel ->
            reel.id == selectedReel.id
        }

        if (index != -1) {
            _state.update { currentState ->
                currentState.copy(currentIndex = index)
            }
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

        loadIfEnabled(NativeAdConfig.REELS)
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
        private const val TAG = "ReelsViewModel"

        private const val DEFAULT_LIKE_COUNT = 105
    }
}