package com.app.videodownloader.presentation.screens.appLanguage.events

import com.app.videodownloader.presentation.screens.splash.events.SplashNavEvents

sealed class AppLanguageNavEvents {
    object NavigateToOnBoarding: AppLanguageNavEvents()
    object ExitApp: AppLanguageNavEvents()
    object NavigateToBack: AppLanguageNavEvents()

    object RecreateActivity : AppLanguageNavEvents()

}