package com.app.videodownloader.presentation.screens.onBoarding.events

import com.app.videodownloader.presentation.screens.appLanguage.events.AppLanguageUiEvents

sealed class OnboardingEvents {
     object NextClicked : OnboardingEvents()
    data class PageChanged(val index: Int) : OnboardingEvents()
     object ContinueClicked : OnboardingEvents()
    object OnPolicyDialogueAcceptClicked : OnboardingEvents()
    object OnBackClicked : OnboardingEvents()

    object OnDialogueExitClicked: OnboardingEvents()
    object OnDialogueCancelCLicked: OnboardingEvents()

}