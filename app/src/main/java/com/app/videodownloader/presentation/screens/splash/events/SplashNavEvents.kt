package com.app.videodownloader.presentation.screens.splash.events

sealed class SplashNavEvents {
    object NavigateToLanguageSRC: SplashNavEvents()
    object NavigateToMainSrc: SplashNavEvents()
    object ExitApp: SplashNavEvents()
}

