package com.app.videodownloader.presentation.screens.onBoarding.events

sealed class OnboardingNavEvent {
    object NavigateToHome : OnboardingNavEvent()
    object ExitApp : OnboardingNavEvent()
}