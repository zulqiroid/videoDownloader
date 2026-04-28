package com.app.videodownloader.presentation.screens.onBoarding.states

import com.app.videodownloader.R

data class OnboardingState(
    val currentPage: Int = 0,
    val pages: List<OnboardingPageModel> = pages(),
    val isLastPage: Boolean = false,
    val showExitDialogue: Boolean = false,
    val showPolicyDialogue: Boolean = false,
)

fun pages() = listOf(
    OnboardingPageModel(
        title = "Welcome to",
        highlight = "Video Downloader",
        description = "Download videos and music from any platform instantly.",
        imageRes = R.drawable.onboarding_src_one_img
    ),
    OnboardingPageModel(
        title = "Watch Trending",
        highlight = "Reels",
        description = "Enjoy and download your favourite reels instantly.",
        imageRes = R.drawable.onboarding_src_two_img
    ),
    OnboardingPageModel(
        title = "Built-in Video",
        highlight = "Player",
        description = "Download and watch your videos and audios easily.",
        imageRes = R.drawable.onboarding_src_three_img
    ),
    OnboardingPageModel(
        title = "",
        highlight = "Video Downloader",
        description = "The advanced downloading engine.",
        imageRes = null,
        showAd = true
    )
)