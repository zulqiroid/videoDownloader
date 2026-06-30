package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.more.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetPrivacyPolicyVisibility
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetRCPremiumIconVisibility
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadNativeAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.more.events.MoreUiEvent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.more.states.MoreState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoreViewModel(
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
    private val getPrivacyPolicyVisibility: GetPrivacyPolicyVisibility,
    private val getRCPremiumIconVisibility: GetRCPremiumIconVisibility,
    ) : ViewModel() {

    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    init {
        getPrivacyVisibility()
        observePremiumStatus()
        viewModelScope.launch {
            getRCPremiumIconVisibility().let { isPremiumIconVisible ->
                _state.update { state ->
                    state.copy(isPremiumvisible = isPremiumIconVisible)
                }
            }
        }
        observeNativeAds()
        observeNativeAdConfig()
    }

    private fun getPrivacyVisibility() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isPrivacyPolicyVisible = getPrivacyPolicyVisibility()
                )
            }
        }
    }

    fun onEvent(event: MoreUiEvent) {
        when (event) {
            MoreUiEvent.ScreenStarted -> {
                loadVisibleNativeAds()
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

        loadIfEnabled(NativeAdConfig.MORE)
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
        private const val TAG = "MoreViewModel"
    }
}