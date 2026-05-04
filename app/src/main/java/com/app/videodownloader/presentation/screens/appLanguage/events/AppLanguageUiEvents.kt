package com.app.videodownloader.presentation.screens.appLanguage.events

import com.app.videodownloader.domain.model.FromWhichSrc
import com.app.videodownloader.presentation.localization.AppLanguageCodes
import com.app.videodownloader.presentation.screens.splash.events.SplashUiEvents

sealed class AppLanguageUiEvents {
    data class OnLanguageItemClicked(val language: AppLanguageCodes): AppLanguageUiEvents()
    data class OnContinueButtonClicked(val src: FromWhichSrc): AppLanguageUiEvents()
    object OnBackClicked : AppLanguageUiEvents()

    object OnDialogueExitClicked: AppLanguageUiEvents()
    object OnDialogueCancelCLicked: AppLanguageUiEvents()
    object OnNavigateBack : AppLanguageUiEvents()
}