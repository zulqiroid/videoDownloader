package com.app.videodownloader.presentation.screens.appLanguage.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.usecases.ads.LoadNativeAdUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdsUseCase
import com.app.videodownloader.presentation.screens.appLanguage.events.AppLanguageNavEvents
import com.app.videodownloader.presentation.screens.appLanguage.events.AppLanguageUiEvents
import com.app.videodownloader.presentation.screens.appLanguage.states.AppLanguageStates
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppLanguageViewModel(
    private val loadNativeAdUseCase: LoadNativeAdUseCase,
    private val observeNativeAdsUseCase: ObserveNativeAdsUseCase,
    private val observeNativeAdConfigUseCase: ObserveNativeAdConfigUseCase
) : ViewModel() {

    private val _states = MutableStateFlow(AppLanguageStates())
    val states = _states.asStateFlow()

    private val _navEvents = MutableSharedFlow<AppLanguageNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    init {
        observeNativeAds()
        observeNativeConfig()
        loadNativeAd()
    }

    private fun observeNativeAds() {
        viewModelScope.launch {
            observeNativeAdsUseCase().collect { ads ->
                _states.update {
                    it.copy(nativeAds = ads)
                }
            }
        }
    }

    private fun observeNativeConfig() {
        viewModelScope.launch {
            observeNativeAdConfigUseCase().collect { config ->
                _states.update {
                    it.copy(nativeAdConfig = config)
                }
            }
        }
    }

    private fun loadNativeAd() {
        viewModelScope.launch {
            loadNativeAdUseCase(
                placementKey = "app_language_list"
            )
        }
    }

    fun onEvent(event: AppLanguageUiEvents) {
        when (event) {
            is AppLanguageUiEvents.OnLanguageItemClicked -> {
                _states.update {
                    it.copy(selectedLanguage = event.language)
                }
            }

            AppLanguageUiEvents.OnContinueButtonClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(AppLanguageNavEvents.NavigateToOnBoarding)
                }
            }

            AppLanguageUiEvents.OnBackClicked -> {
                _states.update {
                    it.copy(showExitDialogue = true)
                }
            }

            AppLanguageUiEvents.OnDialogueCancelCLicked -> {
                _states.update {
                    it.copy(showExitDialogue = false)
                }
            }

            AppLanguageUiEvents.OnDialogueExitClicked -> {
                viewModelScope.launch {
                    _states.update {
                        it.copy(showExitDialogue = false)
                    }
                    _navEvents.emit(AppLanguageNavEvents.ExitApp)
                }
            }
        }
    }
}