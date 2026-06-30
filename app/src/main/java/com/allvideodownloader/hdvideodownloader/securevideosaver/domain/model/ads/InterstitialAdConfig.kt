package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterstitialAdConfig(
    @SerialName("enabled")
    val enabled: Boolean = true,

    @SerialName("interstitial_ad_id")
    val adUnitId: String = TEST_INTERSTITIAL_AD_UNIT_ID,


    @SerialName("splash_unit_id")
    val splashUnitId: String = TEST_INTERSTITIAL_AD_UNIT_ID,


    @SerialName("language_unit_id")
    val languageUnitId: String = TEST_INTERSTITIAL_AD_UNIT_ID,


    @SerialName("intro_unit_id")
    val introUnitId: String = TEST_INTERSTITIAL_AD_UNIT_ID,

    @SerialName("premium_unit_id")
    val premiumUnitId: String = TEST_INTERSTITIAL_AD_UNIT_ID,

    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMs: Long = 45_000L,

    @SerialName("max_ad_cache_duration_ms")
    val maxAdCacheDurationMs: Long = 60 * 60 * 1000L,

    @SerialName("max_load_retry_count")
    val maxLoadRetryCount: Int = 3,

    @SerialName("initial_retry_delay_ms")
    val initialRetryDelayMs: Long = 2_000L,

    @SerialName("max_retry_delay_ms")
    val maxRetryDelayMs: Long = 60_000L,

    @SerialName("show_on_tab_switch")
    val showOnTabSwitch: Boolean = true,

    @SerialName("show_on_play_media")
    val showOnPlayMedia: Boolean = true,

    @SerialName("show_on_download_click")
    val showOnDownloadClick: Boolean = false,

    @SerialName("show_on_reel_open")
    val showOnReelOpen: Boolean = true,

    @SerialName("show_on_social_open")
    val showOnSocialOpen: Boolean = true,

    @SerialName("show_on_premium")
    val showOnPremium: Boolean = true,

    @SerialName("premium_trigger_count")
    val premiumTriggerCount: Int = 1,

    @SerialName("show_on_language")
    val showOnLanguage: Boolean = true,

    @SerialName("show_on_onboarding")
    val showOnOnboarding: Boolean = true,

    @SerialName("onboarding_trigger_count")
    val onboardingTriggerCount: Int = 1,

    @SerialName("language_trigger_count")
    val languageTriggerCount: Int = 1,


    @SerialName("tab_switch_trigger_count")
    val tabSwitchTriggerCount: Int = 3,

    @SerialName("play_media_trigger_count")
    val playMediaTriggerCount: Int = 2,

    @SerialName("download_click_trigger_count")
    val downloadClickTriggerCount: Int = 3,

    @SerialName("reel_open_trigger_count")
    val reelOpenTriggerCount: Int = 2,

    @SerialName("social_open_trigger_count")
    val socialOpenTriggerCount: Int = 2,
    @SerialName("show_on_back_navigation")
    val showOnBackNavigation: Boolean = false,

    @SerialName("back_navigation_trigger_count")
    val backNavigationTriggerCount: Int = 1,
) {
    companion object {
        const val TEST_INTERSTITIAL_AD_UNIT_ID = ""

        fun default(): InterstitialAdConfig = InterstitialAdConfig()
    }
}