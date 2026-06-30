package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsScreens
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdPlacement
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.appUpdate.AppUpdateResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.AdsConsentUseCases
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ClearAllNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.GetAppOpenAdConfigUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.InitializeMobileAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadAppOpenAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadInterstitialAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadNativeAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ShowAppOpenAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ShowInterstitialAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.appUpdate.CheckAppUpdateUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.firstLaunch.FirstLaunchUseCases
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.policy.PolicyUseCases
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.events.SplashNavEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.events.SplashUiEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.states.SplashUiStates
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class SplashViewModel(
    private val firstLaunchUseCases: FirstLaunchUseCases,
    private val policyUseCases: PolicyUseCases,
    private val adsConsentUseCases: AdsConsentUseCases,
    private val initializeMobileAdsUseCase: InitializeMobileAdsUseCase,
    private val loadAppOpenAdUseCase: LoadAppOpenAdUseCase,
    private val showAppOpenAdUseCase: ShowAppOpenAdUseCase,
    private val getAppOpenAdConfigUseCase: GetAppOpenAdConfigUseCase,
    private val loadInterstitialAdUseCase: LoadInterstitialAdUseCase,
    private val showInterstitialAdUseCase: ShowInterstitialAdUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
    private val checkAppUpdateUseCase: CheckAppUpdateUseCase,
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val clearAllNativeAdsUseCase: ClearAllNativeAdsUseCase,
) : ViewModel() {

    private val _adState = MutableStateFlow<AdState>(AdState.Idle)
    val adState = _adState.asStateFlow()

    private val _state = MutableStateFlow(SplashUiStates())
    val states = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<SplashNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    private var isPremiumUser: Boolean = false
    private var hasStartedLaunchFlow = false

    init {
        observePremiumStatus()
        observeNativeAds()
        observeNativeAdConfig()
    }

    fun onEvent(event: SplashUiEvents) {
        when (event) {
            is SplashUiEvents.OnSplashStarted -> onSplashStarted(event.activity)

            is SplashUiEvents.OnGetStartedClicked -> onGetStartedClicked(event.activity)

            SplashUiEvents.OnBackClicked -> {
                if (_state.value.isStarting) return
                _state.update { it.copy(showExitDialogue = true) }
            }

            SplashUiEvents.OnDialogueCancelCLicked -> {
                _state.update { it.copy(showExitDialogue = false) }
            }

            SplashUiEvents.OnDialogueExitClicked -> {
                viewModelScope.launch {
                    _state.update { it.copy(showExitDialogue = false) }
                    clearAllNativeAdsUseCase()
                    _navEvents.emit(SplashNavEvents.ExitApp)
                }
            }
        }
    }

    private fun observePremiumStatus() {
        viewModelScope.launch {
            observeIsPremiumUserUseCase().collect { premium ->
                isPremiumUser = premium

                _state.update { current ->
                    current.copy(
                        isPremiumUser = premium,
                        nativeAds = if (premium) emptyMap() else current.nativeAds
                    )
                }

                if (premium) {
                    clearAllNativeAdsUseCase()
                } else {
                    loadSplashNativeAd()
                }
            }
        }
    }

    private fun observeNativeAds() {
        viewModelScope.launch {
            observeNativeAdsUseCase().collect { nativeAds ->
                _state.update { current ->
                    if (current.isPremiumUser) {
                        current.copy(nativeAds = emptyMap())
                    } else {
                        current.copy(nativeAds = nativeAds)
                    }
                }
            }
        }
    }

    private fun observeNativeAdConfig() {
        viewModelScope.launch {
            observeNativeAdConfigUseCase().collect { config ->
                _state.update { current ->
                    current.copy(nativeAdConfig = config)
                }
                loadSplashNativeAd()
            }
        }
    }

    private fun loadSplashNativeAd() {
        val current = _state.value

        if (current.isPremiumUser) return
        if (!current.isConsentReady) return

        val config = current.nativeAdConfig
        if (!config.enabled) return
        if (config.placement(NativeAdConfig.SPLASH) == null) return

        loadNativeAdUseCase(
            placementKey = NativeAdConfig.SPLASH,
            onStateChanged = { state ->
                _adState.value = state
            }
        )
    }

    private fun onSplashStarted(activity: Activity?) {
        if (hasStartedLaunchFlow) return
        hasStartedLaunchFlow = true

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isStarting = true,
                    isCheckingAppUpdate = true,
                    appUpdateErrorMessage = null,
                    consentErrorMessage = null,
                    isConsentReady = false,
                    pendingDestination = null
                )
            }

            val destination = resolveStartDestination()

            if (activity == null || activity.isFinishing || activity.isDestroyed) {
                _state.update {
                    it.copy(
                        isStarting = false,
                        isCheckingAppUpdate = false,
                        isConsentReady = true,
                        pendingDestination = destination
                    )
                }
                return@launch
            }

            when (val updateResult = checkAppUpdateUseCase(activity)) {
                AppUpdateResult.UpdateStarted -> {
                    _state.update {
                        it.copy(
                            isCheckingAppUpdate = false,
                            isStarting = false
                        )
                    }
                    return@launch
                }

                AppUpdateResult.NoUpdateRequired,
                AppUpdateResult.UpdateNotAvailable -> {
                    _state.update { it.copy(isCheckingAppUpdate = false) }
                }

                is  AppUpdateResult.Failed -> {
                    _state.update {
                        it.copy(
                            isCheckingAppUpdate = false,
                            appUpdateErrorMessage = updateResult.message
                        )
                    }
                }

            }

            continueSplashStartupAfterUpdateCheck(
                activity = activity,
                destination = destination
            )
        }
    }

    private suspend fun continueSplashStartupAfterUpdateCheck(
        activity: Activity,
        destination: SplashNavEvents,
    ) {
        val consentResult = adsConsentUseCases.requestAdsConsentUseCase(activity)

        if (!consentResult.canRequestAds) {
            _state.update {
                it.copy(
                    isStarting = false,
                    isConsentReady = true,
                    pendingDestination = destination,
                    consentErrorMessage = consentResult.errorMessage
                )
            }
            return
        }

        initializeMobileAdsUseCase()

        _state.update {
            it.copy(
                isStarting = false,
                isConsentReady = true,
                pendingDestination = destination,
                consentErrorMessage = null
            )
        }

        loadSplashNativeAd()
    }

    private fun onGetStartedClicked(activity: Activity?) {
        if (_state.value.isStarting) return

        viewModelScope.launch {
            val destination = _state.value.pendingDestination ?: resolveStartDestination()

            _state.update {
                it.copy(
                    isStarting = true,
                    consentErrorMessage = null
                )
            }

            if (activity == null || activity.isFinishing || activity.isDestroyed) {
                navigateAndStopLoading(destination)
                return@launch
            }

            runSplashAdFlow(
                activity = activity,
                destination = destination
            )
        }
    }

    private suspend fun runSplashAdFlow(
        activity: Activity,
        destination: SplashNavEvents
    ) {
        if (isPremiumUser) {
            navigateAndStopLoading(destination)
            return
        }

        val appOpenShown = tryShowAppOpenAd(
            activity = activity,
            destination = destination
        )
        if (appOpenShown) return

        val interstitialShown = tryShowInterstitialAd(
            activity = activity,
            destination = destination
        )
        if (interstitialShown) return

        navigateAndStopLoading(destination)
    }

    private suspend fun tryShowAppOpenAd(
        activity: Activity,
        destination: SplashNavEvents
    ): Boolean {
        if (isPremiumUser) return false

        val appOpenAdConfig = getAppOpenAdConfigUseCase()
        if (!appOpenAdConfig.enabled || !appOpenAdConfig.showOnSplash) return false

        val isLoaded = waitForAppOpenAdLoad()
        if (!isLoaded) return false

        showAppOpenAdUseCase(
            activity = activity,
            forceShow = true,
            onStateChanged = { state -> _adState.value = state },
            onComplete = {
                viewModelScope.launch {
                    navigateAndStopLoading(destination)
                }
            }
        )

        return true
    }

    private suspend fun tryShowInterstitialAd(
        activity: Activity,
        destination: SplashNavEvents
    ): Boolean {
        if (isPremiumUser) return false

        val isLoaded = waitForInterstitialAdLoad()
        if (!isLoaded) return false

        showInterstitialAdUseCase(
            activity = activity,
            placement = InterstitialAdPlacement.Generic,
            forceShow = true,
            onStateChanged = { state -> _adState.value = state },
            onComplete = {
                viewModelScope.launch {
                    navigateAndStopLoading(destination)
                }
            }
        )

        return true
    }

    private suspend fun waitForAppOpenAdLoad(): Boolean {
        return try {
            withTimeout(APP_OPEN_LOAD_TIMEOUT_MS) {
                callbackFlow {
                    loadAppOpenAdUseCase(isSplash = true) { state ->
                        _adState.value = state

                        when (state) {
                            AdState.Loaded -> {
                                trySend(true)
                                close()
                            }

                            is AdState.LoadFailed,
                            is AdState.Skipped,
                            is AdState.ShowFailed -> {
                                trySend(false)
                                close()
                            }

                            else -> Unit
                        }
                    }
                    awaitClose()
                }.collect { }
                true
            }
        } catch (_: TimeoutCancellationException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun waitForInterstitialAdLoad(): Boolean {
        return try {
            withTimeout(INTERSTITIAL_LOAD_TIMEOUT_MS) {
                callbackFlow {
                    loadInterstitialAdUseCase(AdsScreens.Splash) { state ->
                        _adState.value = state

                        when (state) {
                            AdState.Loaded -> {
                                trySend(true)
                                close()
                            }

                            is AdState.LoadFailed,
                            is AdState.Skipped,
                            is AdState.ShowFailed -> {
                                trySend(false)
                                close()
                            }

                            else -> Unit
                        }
                    }
                    awaitClose()
                }.collect { }
                true
            }
        } catch (_: TimeoutCancellationException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun resolveStartDestination(): SplashNavEvents {
        val isFirstLaunch = firstLaunchUseCases.getFirstLaunch().firstOrNull() ?: false
        return if (isFirstLaunch) {
            SplashNavEvents.NavigateToMainSrc
        } else {
            SplashNavEvents.NavigateToLanguageSRC
        }
    }

    private suspend fun navigateAndStopLoading(
        event: SplashNavEvents
    ) {
        _state.update {
            it.copy(isStarting = false)
        }
        _navEvents.emit(event)
    }

    companion object {
        private const val APP_OPEN_LOAD_TIMEOUT_MS = 5_000L
        private const val INTERSTITIAL_LOAD_TIMEOUT_MS = 5_000L
    }
}