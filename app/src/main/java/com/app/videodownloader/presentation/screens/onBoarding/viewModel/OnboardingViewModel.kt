package com.app.videodownloader.presentation.screens.onBoarding.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.R
import com.app.videodownloader.domain.usecases.dataStore.firstLaunch.FirstLaunchUseCases
import com.app.videodownloader.domain.usecases.dataStore.policy.PolicyUseCases
import com.app.videodownloader.presentation.screens.onBoarding.events.OnboardingEvents
import com.app.videodownloader.presentation.screens.onBoarding.events.OnboardingNavEvent
import com.app.videodownloader.presentation.screens.onBoarding.states.OnboardingPageModel
import com.app.videodownloader.presentation.screens.onBoarding.states.OnboardingState
import com.app.videodownloader.presentation.screens.onBoarding.states.pages
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// OnboardingViewModel.kt
class OnboardingViewModel(
    private val firstLaunchUseCases: FirstLaunchUseCases,
    private val policyUseCases: PolicyUseCases
): ViewModel() {



    private val _state = MutableStateFlow(
        OnboardingState()
    )
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<OnboardingNavEvent>()
    val navEvents = _navEvents.asSharedFlow()

    fun onEvent(event: OnboardingEvents) {
        when (event) {

            OnboardingEvents.NextClicked -> {
                val next = _state.value.currentPage + 1
                updatePage(next)
            }

            is OnboardingEvents.PageChanged -> {
                updatePage(event.index)
            }

            OnboardingEvents.ContinueClicked -> {
                viewModelScope.launch {

                    firstLaunchUseCases.setFirstLaunch(true)
                    _navEvents.emit(OnboardingNavEvent.NavigateToHome)

                  /*  _state.update {
                        it.copy(
                            showPolicyDialogue = true
                        )
                    }*/
                }
            }

            OnboardingEvents.OnBackClicked -> {
                _state.update {
                    it.copy(
                        showExitDialogue = true
                    )
                }
            }

            OnboardingEvents.OnDialogueCancelCLicked -> {
                _state.update {
                    it.copy(
                        showExitDialogue = false
                    )
                }
            }

            OnboardingEvents.OnDialogueExitClicked -> {

                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            showExitDialogue = false
                        )
                    }
                    _navEvents.emit(OnboardingNavEvent.ExitApp)
                }
            }

            OnboardingEvents.OnPolicyDialogueAcceptClicked -> {
                viewModelScope.launch {
                    policyUseCases.setPolicyAcceptedUseCase(true)
                    _state.update {
                        it.copy(
                            showPolicyDialogue = false
                        )
                    }
                    _navEvents.emit(OnboardingNavEvent.NavigateToHome)
                }

            }
        }
    }

    private fun updatePage(index: Int) {
        val last = index == pages().lastIndex

        Log.d("onBoardingPage", "the current page is $index")

        _state.update {
            it.copy(
                currentPage = index,
                isLastPage = last
            )
        }
    }
}