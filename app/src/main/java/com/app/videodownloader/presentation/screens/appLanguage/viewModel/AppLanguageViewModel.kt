package com.app.videodownloader.presentation.screens.appLanguage.viewModel

import androidx.lifecycle.ViewModel
import com.app.videodownloader.presentation.screens.appLanguage.states.AppLanguageStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppLanguageViewModel : ViewModel() {
    private val _states = MutableStateFlow(AppLanguageStates())
    val states = _states.asStateFlow()
}