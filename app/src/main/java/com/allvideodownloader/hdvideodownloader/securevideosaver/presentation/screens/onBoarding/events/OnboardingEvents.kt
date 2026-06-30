package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.events

import android.app.Activity

sealed class OnboardingEvents {
    data object NextClicked : OnboardingEvents()
    data class PageChanged(val index: Int) : OnboardingEvents()
    data class ContinueClicked(val activity: Activity) : OnboardingEvents()
    data object OnPolicyDialogueAcceptClicked : OnboardingEvents()
    data object OnBackClicked : OnboardingEvents()
    data object OnDialogueExitClicked : OnboardingEvents()
    data object OnDialogueCancelCLicked : OnboardingEvents()
    data object ScreenStarted : OnboardingEvents()
}