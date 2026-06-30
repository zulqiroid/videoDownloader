package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.splash.events

import android.app.Activity

sealed class SplashUiEvents {

    data class OnSplashStarted(
        val activity: Activity?
    ) : SplashUiEvents()

    data class OnGetStartedClicked(
        val activity: Activity?
    ) : SplashUiEvents()

    object OnBackClicked : SplashUiEvents()

    object OnDialogueExitClicked : SplashUiEvents()

    object OnDialogueCancelCLicked : SplashUiEvents()
}