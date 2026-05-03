package com.app.videodownloader.presentation.screens.splash.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.usecases.ads.GetAppOpenAdConfigUseCase
import com.app.videodownloader.domain.usecases.ads.LoadAppOpenAdUseCase
import com.app.videodownloader.domain.usecases.ads.ShowAppOpenAdUseCase
import com.app.videodownloader.domain.usecases.dataStore.firstLaunch.FirstLaunchUseCases
import com.app.videodownloader.domain.usecases.dataStore.policy.PolicyUseCases
import com.app.videodownloader.presentation.screens.splash.events.SplashNavEvents
import com.app.videodownloader.presentation.screens.splash.events.SplashUiEvents
import com.app.videodownloader.presentation.screens.splash.states.SplashUiStates
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val firstLaunchUseCases: FirstLaunchUseCases,
    private val policyUseCases: PolicyUseCases,
    private val loadAppOpenAdUseCase: LoadAppOpenAdUseCase,
    private val showAppOpenAdUseCase: ShowAppOpenAdUseCase,
    private val getAppOpenAdConfigUseCase: GetAppOpenAdConfigUseCase,
) : ViewModel() {

    private val _adState = MutableStateFlow<AdState>(AdState.Idle)
    val adState = _adState.asStateFlow()

    private val _state = MutableStateFlow(SplashUiStates())
    val states = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<SplashNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    init {
        loadAppOpenAd()
    }

    fun onEvent(event: SplashUiEvents) {
        when (event) {
            is SplashUiEvents.OnGetStartedClicked -> {
                onGetStartedClicked(event.activity)
            }

            SplashUiEvents.OnBackClicked -> {
                _state.update {
                    it.copy(showExitDialogue = true)
                }
            }

            SplashUiEvents.OnDialogueCancelCLicked -> {
                _state.update {
                    it.copy(showExitDialogue = false)
                }
            }

            SplashUiEvents.OnDialogueExitClicked -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(showExitDialogue = false)
                    }
                    _navEvents.emit(SplashNavEvents.ExitApp)
                }
            }
        }
    }

    private fun onGetStartedClicked(activity: Activity?) {
        viewModelScope.launch {
            val destination = resolveStartDestination()
            val appOpenAdConfig = getAppOpenAdConfigUseCase()

            if (
                activity == null ||
                !appOpenAdConfig.enabled ||
                !appOpenAdConfig.showOnSplash
            ) {
                navigate(destination)
                return@launch
            }

            showAppOpenAdUseCase(
                activity = activity,
                forceShow = true,
                onStateChanged = { state ->
                    _adState.value = state
                },
                onComplete = {
                    viewModelScope.launch {
                        navigate(destination)
                    }
                }
            )
        }
    }

    private suspend fun resolveStartDestination(): SplashNavEvents {
        val isFirstLaunch = firstLaunchUseCases
            .getFirstLaunch()
            .firstOrNull() ?: false

        return if (isFirstLaunch) {
            SplashNavEvents.NavigateToMainSrc
        } else {
            SplashNavEvents.NavigateToLanguageSRC
        }
    }

    private suspend fun navigate(event: SplashNavEvents) {
        _navEvents.emit(event)
    }

    fun loadAppOpenAd() {
        loadAppOpenAdUseCase { state ->
            _adState.value = state
        }
    }
}