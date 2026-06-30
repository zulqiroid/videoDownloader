package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadNativeAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.events.DownloadGuideIntent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.events.DownloadGuideNavEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.state.DownloadGuideState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.state.GuideStep
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloadGuideViewModel(
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        DownloadGuideState(
            steps = listOf(
                GuideStep(
                    titleRes = R.string.download_guide_step_copy_link_title,
                    descriptionRes = R.string.download_guide_step_copy_link_description,
                    icon = R.drawable.ic_audio
                ),
                GuideStep(
                    titleRes = R.string.download_guide_step_paste_analyze_title,
                    descriptionRes = R.string.download_guide_step_paste_analyze_description,
                    icon = R.drawable.ic_audio
                ),
                GuideStep(
                    titleRes = R.string.download_guide_step_select_save_title,
                    descriptionRes = R.string.download_guide_step_select_save_description,
                    icon = R.drawable.ic_audio
                )
            )
        )
    )

    val state: StateFlow<DownloadGuideState> = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<DownloadGuideNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    init {
        observePremiumStatus()
        observeNativeAds()
        observeNativeAdConfig()
    }


    fun onIntent(intent: DownloadGuideIntent) {
        when (intent) {
            DownloadGuideIntent.OnBackClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(DownloadGuideNavEvents.NavigateToBackScreen)
                }
            }

            DownloadGuideIntent.OnGotItClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(DownloadGuideNavEvents.NavigateToBackScreen)
                }
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

        loadIfEnabled(NativeAdConfig.DOWNLOAD_GUIDE)
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
        private const val TAG = "DownloadGuideViewModel"
    }
}