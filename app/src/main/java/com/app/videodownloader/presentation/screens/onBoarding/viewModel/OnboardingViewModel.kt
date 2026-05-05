package com.app.videodownloader.presentation.screens.onBoarding.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.usecases.ads.ClearAllNativeAdsUseCase
import com.app.videodownloader.domain.usecases.ads.LoadNativeAdUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdsUseCase
import com.app.videodownloader.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.app.videodownloader.domain.usecases.dataStore.firstLaunch.FirstLaunchUseCases
import com.app.videodownloader.domain.usecases.dataStore.policy.PolicyUseCases
import com.app.videodownloader.presentation.screens.onBoarding.events.OnboardingEvents
import com.app.videodownloader.presentation.screens.onBoarding.events.OnboardingNavEvent
import com.app.videodownloader.presentation.screens.onBoarding.states.OnboardingState
import com.app.videodownloader.presentation.screens.onBoarding.states.buildOnboardingPages
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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

    init {
        observePremiumStatus()
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

            OnboardingEvents.ContinueClicked -> {
                viewModelScope.launch {
                    firstLaunchUseCases.setFirstLaunch(true)
                    clearAllNativeAdsUseCase()
                    _navEvents.emit(OnboardingNavEvent.NavigateToHome)
                }
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
                        loadVisibleOnboardingAds()
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
        index: Int
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