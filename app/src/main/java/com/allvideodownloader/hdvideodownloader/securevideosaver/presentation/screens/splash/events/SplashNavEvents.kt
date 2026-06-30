package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.events

sealed class SplashNavEvents {
    object NavigateToLanguageSRC: SplashNavEvents()
    object NavigateToMainSrc: SplashNavEvents()
    object ExitApp: SplashNavEvents()
}

