package com.app.videodownloader.presentation.screens.onBoarding.events

sealed class OnboardingEvents {
    data object NextClicked : OnboardingEvents()
    data class PageChanged(val index: Int) : OnboardingEvents()
    data object ContinueClicked : OnboardingEvents()
    data object OnPolicyDialogueAcceptClicked : OnboardingEvents()
    data object OnBackClicked : OnboardingEvents()
    data object OnDialogueExitClicked : OnboardingEvents()
    data object OnDialogueCancelCLicked : OnboardingEvents()
    data object ScreenStarted : OnboardingEvents()
}