package com.app.videodownloader.presentation.screens.splash.states

import com.app.videodownloader.presentation.screens.splash.events.SplashNavEvents

data class SplashUiStates(
    val showExitDialogue: Boolean = false,
    val isStarting: Boolean = false,
    val consentErrorMessage: String? = null,
    val isConsentReady: Boolean = false,
    val pendingDestination: SplashNavEvents? = null,

    val isCheckingAppUpdate: Boolean = false,
    val appUpdateErrorMessage: String? = null,
)