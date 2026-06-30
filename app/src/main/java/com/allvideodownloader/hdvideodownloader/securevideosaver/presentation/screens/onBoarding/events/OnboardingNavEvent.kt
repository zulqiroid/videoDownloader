package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.events

sealed class OnboardingNavEvent {
    object NavigateToHome : OnboardingNavEvent()
    object ExitApp : OnboardingNavEvent()
}