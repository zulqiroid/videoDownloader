package com.app.videodownloader.presentation.screens.onBoarding.states
data class OnboardingPageModel(
    val title: String,
    val highlight: String,
    val description: String,
    val imageRes: Int?,
    val showAd: Boolean = false
)