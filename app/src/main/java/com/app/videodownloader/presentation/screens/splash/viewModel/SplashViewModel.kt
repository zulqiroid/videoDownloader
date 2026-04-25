package com.app.videodownloader.presentation.screens.splash.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.presentation.screens.splash.events.SplashNavEvents
import com.app.videodownloader.presentation.screens.splash.events.SplashUiEvents
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel: ViewModel() {

    private val _navEvents = MutableSharedFlow<SplashNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    fun onEvent(events: SplashUiEvents){
        when(events){
            SplashUiEvents.OnGetStartedClicked -> {
                viewModelScope.launch {
                    _navEvents.emit(SplashNavEvents.NavigateToLanguageSRC)
                }
            }
        }
    }
}