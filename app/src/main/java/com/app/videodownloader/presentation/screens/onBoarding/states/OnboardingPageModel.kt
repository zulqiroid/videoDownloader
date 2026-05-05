package com.app.videodownloader.presentation.screens.onBoarding.states

import androidx.annotation.StringRes

data class OnboardingPageModel(
    @StringRes val titleRes: Int,
    @StringRes val highlightRes: Int,
    @StringRes val descriptionRes: Int,
    val imageRes: Int?,
    val nativeAdPlacementKey: String?,
    val type: OnboardingPageType = OnboardingPageType.Content
)

enum class OnboardingPageType {
    Content,
    FullNativeAd
}