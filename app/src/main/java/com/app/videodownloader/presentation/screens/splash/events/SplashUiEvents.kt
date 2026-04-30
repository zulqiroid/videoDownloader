package com.app.videodownloader.presentation.screens.splash.events

import android.app.Activity

sealed class SplashUiEvents {
    data class OnGetStartedClicked(
        val activity : Activity?
    ) : SplashUiEvents()
    object OnBackClicked : SplashUiEvents()

    object OnDialogueExitClicked: SplashUiEvents()
    object OnDialogueCancelCLicked: SplashUiEvents()
}