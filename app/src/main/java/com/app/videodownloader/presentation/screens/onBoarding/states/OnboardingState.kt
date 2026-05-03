package com.app.videodownloader.presentation.screens.onBoarding.states

import com.app.videodownloader.R
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.google.android.gms.ads.nativead.NativeAd

data class OnboardingState(
    val currentPage: Int = 0,
    val pages: List<OnboardingPageModel> = emptyList(),
    val isLastPage: Boolean = false,
    val showExitDialogue: Boolean = false,
    val showPolicyDialogue: Boolean = false,
    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default()
)

fun buildOnboardingPages(
    nativeAdConfig: NativeAdConfig
): List<OnboardingPageModel> {
    val pages = mutableListOf<OnboardingPageModel>()

    pages += OnboardingPageModel(
        title = "Welcome to",
        highlight = "Video Downloader",
        description = "Download videos and music from any platform instantly.",
        imageRes = R.drawable.onboarding_src_one_img,
        nativeAdPlacementKey = NativeAdConfig.ONBOARDING_STEP_1
    )

    pages += OnboardingPageModel(
        title = "Watch Trending",
        highlight = "Reels",
        description = "Enjoy and download your favourite reels instantly.",
        imageRes = R.drawable.onboarding_src_two_img,
        nativeAdPlacementKey = NativeAdConfig.ONBOARDING_STEP_2
    )

    pages += OnboardingPageModel(
        title = "Built-in Video",
        highlight = "Player",
        description = "Download and watch your videos and audios easily.",
        imageRes = R.drawable.onboarding_src_three_img,
        nativeAdPlacementKey = NativeAdConfig.ONBOARDING_STEP_3
    )

    val shouldShowFullNativePage = nativeAdConfig
        .placement(NativeAdConfig.ONBOARDING_STEP_4) != null

    if (shouldShowFullNativePage) {
        pages += OnboardingPageModel(
            title = "",
            highlight = "Video Downloader",
            description = "The advanced downloading engine.",
            imageRes = null,
            nativeAdPlacementKey = NativeAdConfig.ONBOARDING_STEP_4,
            type = OnboardingPageType.FullNativeAd
        )
    }

    return pages
}