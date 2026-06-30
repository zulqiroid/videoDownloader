package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.events

sealed class AppLanguageNavEvents {
    object NavigateToOnBoarding: AppLanguageNavEvents()
    object ExitApp: AppLanguageNavEvents()
    object NavigateToBack: AppLanguageNavEvents()

    object RecreateActivity : AppLanguageNavEvents()

}