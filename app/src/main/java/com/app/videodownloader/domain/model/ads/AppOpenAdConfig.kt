package com.app.videodownloader.domain.model.ads

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppOpenAdConfig(
    @SerialName("enabled")
    val enabled: Boolean = true,

    @SerialName("show_on_resume")
    val showOnResume: Boolean = true,

    @SerialName("show_on_splash")
    val showOnSplash: Boolean = true,

    @SerialName("ad_unit_id")
    val adUnitId: String = TEST_APP_OPEN_AD_UNIT_ID,

    @SerialName("max_ad_cache_duration_ms")
    val maxAdCacheDurationMs: Long = 4 * 60 * 60 * 1000L,

    @SerialName("min_interval_between_shows_ms")
    val minIntervalBetweenShowsMs: Long = 0L,

    @SerialName("min_background_duration_before_show_ms")
    val minBackgroundDurationBeforeShowMs: Long = 0L,

    @SerialName("max_load_retry_count")
    val maxLoadRetryCount: Int = 3,

    @SerialName("initial_retry_delay_ms")
    val initialRetryDelayMs: Long = 2_000L,

    @SerialName("max_retry_delay_ms")
    val maxRetryDelayMs: Long = 60_000L,

    @SerialName("show_automatically_on_cold_start")
    val showAutomaticallyOnColdStart: Boolean = false
) {
    companion object {
        const val TEST_APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"

        fun default(): AppOpenAdConfig = AppOpenAdConfig()
    }
}