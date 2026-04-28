package com.app.videodownloader.presentation.screens.splash.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiStates())
    val states = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<SplashNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    fun onEvent(events: SplashUiEvents) {
        when (events) {
            SplashUiEvents.OnGetStartedClicked -> {
                viewModelScope.launch {

                    val isFirstLaunch = firstLaunchUseCases.getFirstLaunch().firstOrNull()


                    if (isFirstLaunch != null && isFirstLaunch){
                         _navEvents.emit(SplashNavEvents.NavigateToMainSrc)

                    }else{
                        _navEvents.emit(SplashNavEvents.NavigateToLanguageSRC)
                    }

                }
            }

            SplashUiEvents.OnBackClicked -> {
                _state.update {
                    it.copy(
                        showExitDialogue = true
                    )
                }
            }

            SplashUiEvents.OnDialogueCancelCLicked -> {
                _state.update {
                    it.copy(
                        showExitDialogue = false
                    )
                }
            }

            SplashUiEvents.OnDialogueExitClicked -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            showExitDialogue = false
                        )
                    }
                    _navEvents.emit(SplashNavEvents.ExitApp)
                }
            }

        }
    }
}