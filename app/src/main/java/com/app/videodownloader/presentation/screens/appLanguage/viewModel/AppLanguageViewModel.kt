package com.app.videodownloader.presentation.screens.appLanguage.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.model.FromWhichSrc
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.usecases.ads.LoadNativeAdUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdPoolsUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdsUseCase
import com.app.videodownloader.domain.usecases.billing.ObserveIsPremiumUserUseCase
import com.app.videodownloader.domain.usecases.dataStore.appLanguage.GetSelectedLanguageUseCase
import com.app.videodownloader.domain.usecases.dataStore.appLanguage.SaveSelectedLanguageUseCase
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdSlotHelper
import com.app.videodownloader.presentation.localization.AppLanguageCodes
import com.app.videodownloader.presentation.screens.appLanguage.events.AppLanguageNavEvents
import com.app.videodownloader.presentation.screens.appLanguage.events.AppLanguageUiEvents
import com.app.videodownloader.presentation.screens.appLanguage.states.AppLanguageStates
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppLanguageViewModel(
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdPoolsUseCase: ObserveNativeAdPoolsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase,
    private val getSelectedLanguageUseCase: GetSelectedLanguageUseCase,
    private val saveSelectedLanguageUseCase: SaveSelectedLanguageUseCase,
    private val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
) : ViewModel() {

    private val _states = MutableStateFlow(AppLanguageStates())
    val states = _states.asStateFlow()

    private val _navEvents = MutableSharedFlow<AppLanguageNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    init {
        loadSavedLanguage()
        observePremiumStatus()
        observeNativeAds()
        observeNativeAdPools()
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
                            nativeAdPools = if (isPremium) emptyMap() else currentState.nativeAdPools
                        )
                    }

                    if (!isPremium) {
                        loadAppLanguageNativeSlots()
                    } else {
                        Log.d(TAG, "App language native ads skipped: premium user")
                    }
                }
        }
    }

    private fun observeNativeAdPools() {
        viewModelScope.launch {
            observeNativeAdPoolsUseCase().collect { pools ->
                _states.update { currentState ->
                    if (currentState.isPremiumUser) {
                        currentState.copy(nativeAdPools = emptyMap())
                    } else {
                        currentState.copy(nativeAdPools = pools)
                    }
                }
            }
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

        val totalItems = AppLanguageCodes.entries.size

        val slotKeys = NativeAdSlotHelper.insertionSlotKeys(
            totalItems = totalItems,
            config = placementConfig
        )

        Log.d(
            TAG,
            "App language native slots to load: $slotKeys, totalItems=$totalItems"
        )

        slotKeys.forEach { slotKey ->
            loadNativeAdUseCase(
                placementKey = placementKey,
                slotKey = slotKey,
                onStateChanged = { adState ->
                    Log.d(
                        TAG,
                        "App language native ad state. placement=$placementKey slot=$slotKey state=$adState"
                    )
                }
            )
        }
    }

    fun onEvent(event: AppLanguageUiEvents) {
        when (event) {
            is AppLanguageUiEvents.OnLanguageItemClicked -> {
                onLanguageSelected(event.language)
            }

            is AppLanguageUiEvents.OnContinueButtonClicked -> {
                onContinueClicked(event.src)
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
                    _states.update{
                        it.copy(
                            selectedLanguage =  AppLanguageCodes.DEFAULT
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

    private fun onContinueClicked(fromWhichScreen: FromWhichSrc) {
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