package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.FromWhichSrc
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AdsScreens
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdPlacement
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ClearAllNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadInterstitialAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.LoadNativeAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdPoolsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ObserveNativeAdsUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads.ShowInterstitialAdUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.appLanguage.GetSelectedLanguageUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.appLanguage.SaveSelectedLanguageUseCase
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdSlotHelper
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLanguageCodes
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.events.AppLanguageNavEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.events.AppLanguageUiEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.states.AppLanguageStates
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.events.OnboardingNavEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppLanguageViewModel(
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
     private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val getSelectedLanguageUseCase: GetSelectedLanguageUseCase,
    private val saveSelectedLanguageUseCase: SaveSelectedLanguageUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,

    private val clearAllNativeAdsUseCase: ClearAllNativeAdsUseCase,

    private val loadInterstitialAd: LoadInterstitialAdUseCase,
    private val showInterstitialAd: ShowInterstitialAdUseCase,
) : ViewModel() {

    private val _states = MutableStateFlow(AppLanguageStates())
    val states = _states.asStateFlow()

    private val _navEvents = MutableSharedFlow<AppLanguageNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    private val _adState = MutableStateFlow<AdState>(AdState.Idle)
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    init {
        loadSavedLanguage()
        observePremiumStatus()
        observeNativeAds()
        observeNativeConfig()
    }

    private fun loadSavedLanguage() {
        viewModelScope.launch {
            val savedLanguage = getSelectedLanguageUseCase()

            _states.update { currentState ->
                currentState.copy(
                    selectedLanguage = savedLanguage
                )
            }
        }
    }

    private fun observeNativeAds() {
        viewModelScope.launch {
            observeNativeAdsUseCase().collect { ads ->
                _states.update { currentState ->
                    if (currentState.isPremiumUser) {
                        currentState.copy(nativeAds = emptyMap())
                    } else {
                        currentState.copy(nativeAds = ads)
                    }
                }
            }
        }
    }


    private fun observePremiumStatus() {
        viewModelScope.launch {
            observeIsPremiumUserUseCase()
                .distinctUntilChanged()
                .collect { isPremium ->
                    _states.update { currentState ->
                        currentState.copy(
                            isPremiumUser = isPremium,
                            nativeAds = if (isPremium) emptyMap() else currentState.nativeAds,
                         )
                    }

                    if (!isPremium) {
                        preloadInterstitialAd()
                        loadAppLanguageNativeSlots()
                    } else {
                        clearAllNativeAdsUseCase()
                        Log.d(TAG, "App language native ads skipped: premium user")
                    }
                }
        }
    }

    fun preloadInterstitialAd() {
        if (_states.value.isPremiumUser) {
            _adState.value = AdState.Skipped(
                reason = "Interstitial preload skipped: premium user"
            )
            return
        }

        loadInterstitialAd(AdsScreens.OnBoarding) { state ->
            _adState.value = state
        }
    }


    private fun observeNativeConfig() {
        viewModelScope.launch {
            observeNativeAdConfigUseCase().collect { config ->
                _states.update { currentState ->
                    currentState.copy(nativeAdConfig = config)
                }

                loadAppLanguageNativeSlots()
            }
        }
    }

    private fun loadAppLanguageNativeSlots() {
        val currentState = _states.value

        if (currentState.isPremiumUser) {
            Log.d(TAG, "App language native ad load skipped: premium user")
            return
        }

        val placementKey = NativeAdConfig.APP_LANGUAGE_LIST
        val placementConfig = currentState.nativeAdConfig.placement(placementKey)

        if (placementConfig == null) {
            Log.d(TAG, "App language native skipped: placement disabled or missing.")
            return
        }

        loadNativeAdUseCase(
            placementKey = placementKey,
            slotKey = NativeAdConfig.DEFAULT_SLOT,
            onStateChanged = { adState ->
                Log.d(
                    TAG,
                    "App language native ad state. placement=$placementKey slot=${NativeAdConfig.DEFAULT_SLOT} state=$adState"
                )
            }
        )
    }

    fun onEvent(event: AppLanguageUiEvents) {
        when (event) {
            is AppLanguageUiEvents.OnLanguageItemClicked -> {
                onLanguageSelected(event.language)
            }

            is AppLanguageUiEvents.OnContinueButtonClicked -> {
                onContinueClicked(activity = event.activity, fromWhichScreen = event.src)
            }

            AppLanguageUiEvents.OnBackClicked -> {
                showExitDialog()
            }

            AppLanguageUiEvents.OnDialogueCancelCLicked -> {
                hideExitDialog()
            }

            AppLanguageUiEvents.OnDialogueExitClicked -> {
                onExitClicked()
            }

            AppLanguageUiEvents.OnNavigateBack -> {
                viewModelScope.launch {
                    _navEvents.emit(AppLanguageNavEvents.NavigateToBack)
                    _states.update {
                        it.copy(
                            selectedLanguage = AppLanguageCodes.DEFAULT
                        )
                    }
                }
            }
        }
    }

    private fun onLanguageSelected(language: AppLanguageCodes) {
        _states.update { currentState ->
            currentState.copy(
                selectedLanguage = language
            )
        }
    }

    private fun onContinueClicked(activity: Activity, fromWhichScreen: FromWhichSrc) {


        showInterstitialAd(
            activity = activity,
            placement = InterstitialAdPlacement.OnBoarding,
            forceShow = false,
            onStateChanged = { state ->
                _adState.value = state
            },
            onComplete = {
                viewModelScope.launch {
                    val selectedLanguage = _states.value.selectedLanguage

                    saveSelectedLanguageUseCase(selectedLanguage)

                    when (fromWhichScreen) {
                        FromWhichSrc.FROM_MAIN -> {
                            _navEvents.emit(AppLanguageNavEvents.RecreateActivity)
                        }

                        FromWhichSrc.FROM_SPLASH -> {
                            _navEvents.emit(AppLanguageNavEvents.NavigateToOnBoarding)
                        }

                        else -> Unit
                    }
                }
            }
        )
    }

    private fun showExitDialog() {
        _states.update { currentState ->
            currentState.copy(showExitDialogue = true)
        }
    }

    private fun hideExitDialog() {
        _states.update { currentState ->
            currentState.copy(showExitDialogue = false)
        }
    }

    private fun onExitClicked() {
        viewModelScope.launch {
            _states.update { currentState ->
                currentState.copy(showExitDialogue = false)
            }

            _navEvents.emit(AppLanguageNavEvents.ExitApp)
        }
    }

    companion object {
        private const val TAG = "AppLanguageViewModel"
    }
}