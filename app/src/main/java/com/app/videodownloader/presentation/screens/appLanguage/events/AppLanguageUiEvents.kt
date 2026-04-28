package com.app.videodownloader.presentation.screens.appLanguage.events

import com.app.videodownloader.presentation.localization.AppLanguageCodes
import com.app.videodownloader.presentation.screens.splash.events.SplashUiEvents

sealed class AppLanguageUiEvents {
    data class OnLanguageItemClicked(val language: AppLanguageCodes): AppLanguageUiEvents()
    object OnContinueButtonClicked: AppLanguageUiEvents()
    object OnBackClicked : AppLanguageUiEvents()

    object OnDialogueExitClicked: AppLanguageUiEvents()
    object OnDialogueCancelCLicked: AppLanguageUiEvents()
}