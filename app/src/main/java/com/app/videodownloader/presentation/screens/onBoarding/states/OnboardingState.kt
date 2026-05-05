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

    val isPremiumUser: Boolean = false,

    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default()
)

fun buildOnboardingPages(
    nativeAdConfig: NativeAdConfig,
    isPremiumUser: Boolean = false,
): List<OnboardingPageModel> {
    val pages = mutableListOf<OnboardingPageModel>()

    pages += OnboardingPageModel(
        titleRes = R.string.onboarding_welcome_to,
        highlightRes = R.string.onboarding_video_downloader,
        descriptionRes = R.string.onboarding_download_videos_music,
        imageRes = R.drawable.onboarding_src_one_img,
        nativeAdPlacementKey = if (isPremiumUser) null else NativeAdConfig.ONBOARDING_STEP_1
    )

    pages += OnboardingPageModel(
        titleRes = R.string.onboarding_watch_trending,
        highlightRes = R.string.onboarding_reels,
        descriptionRes = R.string.onboarding_enjoy_download_reels,
        imageRes = R.drawable.onboarding_src_two_img,
        nativeAdPlacementKey = if (isPremiumUser) null else NativeAdConfig.ONBOARDING_STEP_2
    )

    pages += OnboardingPageModel(
        titleRes = R.string.onboarding_built_in_video,
        highlightRes = R.string.onboarding_player,
        descriptionRes = R.string.onboarding_download_watch_videos_audios,
        imageRes = R.drawable.onboarding_src_three_img,
        nativeAdPlacementKey = if (isPremiumUser) null else NativeAdConfig.ONBOARDING_STEP_3
    )

    val shouldShowFullNativePage =
        !isPremiumUser &&
                nativeAdConfig.placement(NativeAdConfig.ONBOARDING_STEP_4) != null

    if (shouldShowFullNativePage) {
        pages += OnboardingPageModel(
            titleRes = R.string.empty_string,
            highlightRes = R.string.onboarding_video_downloader,
            descriptionRes = R.string.onboarding_advanced_downloading_engine,
            imageRes = null,
            nativeAdPlacementKey = NativeAdConfig.ONBOARDING_STEP_4,
            type = OnboardingPageType.FullNativeAd
        )
    }

    return pages
}