package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.events

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.FromWhichSrc
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLanguageCodes

sealed class AppLanguageUiEvents {
    data class OnLanguageItemClicked(val language: AppLanguageCodes): AppLanguageUiEvents()
    data class OnContinueButtonClicked(val activity: Activity, val src: FromWhichSrc): AppLanguageUiEvents()
    object OnBackClicked : AppLanguageUiEvents()

    object OnDialogueExitClicked: AppLanguageUiEvents()
    object OnDialogueCancelCLicked: AppLanguageUiEvents()
    object OnNavigateBack : AppLanguageUiEvents()
}