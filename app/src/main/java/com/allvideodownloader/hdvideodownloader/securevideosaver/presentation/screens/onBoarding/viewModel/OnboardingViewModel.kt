package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsScreens
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdPlacement
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.GetRCPremiumIconVisibility
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ClearAllNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadInterstitialAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadNativeAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ShowInterstitialAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.firstLaunch.FirstLaunchUseCases
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.policy.PolicyUseCases
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.events.OnboardingEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.events.OnboardingNavEvent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.states.OnboardingState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.states.buildOnboardingPages
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.events.PremiumIntent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val firstLaunchUseCases: FirstLaunchUseCases,
    private val policyUseCases: PolicyUseCases,
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val clearAllNativeAdsUseCase: ClearAllNativeAdsUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
    private val loadInterstitialAd: LoadInterstitialAdUseCase,
    private val showInterstitialAd: ShowInterstitialAdUseCase,
    private val getRCPremiumIconVisibility: GetRCPremiumIconVisibility,
    ) : ViewModel() {

    private val _state = MutableStateFlow(
        OnboardingState(
            pages = buildOnboardingPages(
                nativeAdConfig = NativeAdConfig.default(),
                isPremiumUser = false
            )
        )
    )
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<OnboardingNavEvent>()
    val navEvents = _navEvents.asSharedFlow()

    private val _adState = MutableStateFlow<AdState>(AdState.Idle)
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    init {
        observePremiumStatus()
        viewModelScope.launch {
            getRCPremiumIconVisibility().let { isPremiumIconVisible ->
                _state.update { state ->
                    state.copy(isPremiumPageVisible = isPremiumIconVisible)
                }
            }
        }
        observeNativeAds()
        observeNativeAdConfig()
    }

    fun onEvent(event: OnboardingEvents) {
        when (event) {
            OnboardingEvents.ScreenStarted -> {
                loadVisibleOnboardingAds()
            }

            OnboardingEvents.NextClicked -> {
                val nextPage = _state.value.currentPage + 1
                updatePage(nextPage)
            }

            is OnboardingEvents.PageChanged -> {
                updatePage(event.index)
            }

            is OnboardingEvents.ContinueClicked -> {

                showInterstitialAd(
                    activity = event.activity,
                    placement = InterstitialAdPlacement.OnBoarding,
                    forceShow = false,
                    onStateChanged = { state ->
                        _adState.value = state
                    },
                    onComplete = {
                        viewModelScope.launch {
                            firstLaunchUseCases.setFirstLaunch(true)
                            clearAllNativeAdsUseCase()
                            _navEvents.emit(OnboardingNavEvent.NavigateToHome)
                        }
                    }
                )

            }

            OnboardingEvents.OnBackClicked -> {
                _state.update {
                    it.copy(showExitDialogue = true)
                }
            }

            OnboardingEvents.OnDialogueCancelCLicked -> {
                _state.update {
                    it.copy(showExitDialogue = false)
                }
            }

            OnboardingEvents.OnDialogueExitClicked -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(showExitDialogue = false)
                    }
                    clearAllNativeAdsUseCase()
                    _navEvents.emit(OnboardingNavEvent.ExitApp)
                }
            }

            OnboardingEvents.OnPolicyDialogueAcceptClicked -> {
                viewModelScope.launch {
                    policyUseCases.setPolicyAcceptedUseCase(true)
                    _state.update {
                        it.copy(showPolicyDialogue = false)
                    }
                    clearAllNativeAdsUseCase()
                    _navEvents.emit(OnboardingNavEvent.NavigateToHome)
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
                        val updatedPages = buildOnboardingPages(
                            nativeAdConfig = currentState.nativeAdConfig,
                            isPremiumUser = isPremium
                        )

                        val safeCurrentPage = currentState.currentPage.coerceIn(
                            minimumValue = 0,
                            maximumValue = updatedPages.lastIndex.coerceAtLeast(0)
                        )

                        currentState.copy(
                            isPremiumUser = isPremium,
                            nativeAds = if (isPremium) emptyMap() else currentState.nativeAds,
                            pages = updatedPages,
                            currentPage = safeCurrentPage,
                            isLastPage = safeCurrentPage == updatedPages.lastIndex
                        )
                    }

                    if (isPremium) {
                        clearAllNativeAdsUseCase()
                        Log.d(TAG, "Onboarding native ads skipped: premium user")
                    } else {
                        preloadInterstitialAd()
                        loadVisibleOnboardingAds()
                    }
                }
        }
    }

    fun preloadInterstitialAd() {
        if (_state.value.isPremiumUser) {
            _adState.value = AdState.Skipped(
                reason = "Interstitial preload skipped: premium user"
            )
            return
        }

        loadInterstitialAd(AdsScreens.OnBoarding) { state ->
            _adState.value = state
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
                _state.update { currentState ->
                    val updatedPages = buildOnboardingPages(
                        nativeAdConfig = config,
                        isPremiumUser = currentState.isPremiumUser
                    )

                    val safeCurrentPage = currentState.currentPage.coerceIn(
                        minimumValue = 0,
                        maximumValue = updatedPages.lastIndex.coerceAtLeast(0)
                    )

                    currentState.copy(
                        nativeAdConfig = config,
                        pages = updatedPages,
                        currentPage = safeCurrentPage,
                        isLastPage = safeCurrentPage == updatedPages.lastIndex
                    )
                }

                loadVisibleOnboardingAds()
            }
        }
    }

    private fun loadVisibleOnboardingAds() {
        val state = _state.value

        if (state.isPremiumUser) {
            Log.d(TAG, "Onboarding native ad load skipped: premium user")
            return
        }

        val config = state.nativeAdConfig

        if (!config.enabled) return

        state.pages
            .mapNotNull { it.nativeAdPlacementKey }
            .filter { placementKey ->
                config.placement(placementKey) != null
            }
            .forEach { placementKey ->
                loadNativeAdUseCase(
                    placementKey = placementKey,
                    onStateChanged = { adState ->
                        Log.d(TAG, "Native ad state. placement=$placementKey state=$adState")
                    }
                )
            }
    }

    private fun updatePage(
        index: Int,
    ) {
        val pages = _state.value.pages

        if (pages.isEmpty()) return

        val pageIndex = index.coerceIn(
            minimumValue = 0,
            maximumValue = pages.lastIndex
        )

        _state.update {
            it.copy(
                currentPage = pageIndex,
                isLastPage = pageIndex == pages.lastIndex
            )
        }
    }

    override fun onCleared() {
        clearAllNativeAdsUseCase()
        super.onCleared()
    }

    companion object {
        private const val TAG = "OnboardingViewModel"
    }
}