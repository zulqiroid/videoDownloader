package com.app.videodownloader.presentation.screens.splash.events

sealed class SplashUiEvents {
    object OnGetStartedClicked : SplashUiEvents()
    object OnBackClicked : SplashUiEvents()

    object OnDialogueExitClicked: SplashUiEvents()
    object OnDialogueCancelCLicked: SplashUiEvents()
}