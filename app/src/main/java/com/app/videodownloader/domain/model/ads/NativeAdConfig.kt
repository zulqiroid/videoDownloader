package com.app.videodownloader.domain.model.ads

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NativeAdConfig(
    @SerialName("enabled")
    val enabled: Boolean = true,

    @SerialName("ad_unit_id")
    val adUnitId: String = TEST_NATIVE_AD_UNIT_ID,

    @SerialName("max_ad_cache_duration_ms")
    val maxAdCacheDurationMs: Long = 60 * 60 * 1000L,

    @SerialName("max_load_retry_count")
    val maxLoadRetryCount: Int = 3,

    @SerialName("initial_retry_delay_ms")
    val initialRetryDelayMs: Long = 2_000L,

    @SerialName("max_retry_delay_ms")
    val maxRetryDelayMs: Long = 60_000L,

    @SerialName("container_background_color")
    val containerBackgroundColor: String = "#FFFFFF",

    @SerialName("container_border_color")
    val containerBorderColor: String = "#DADADA",

    @SerialName("container_border_width_dp")
    val containerBorderWidthDp: Int = 1,

    @SerialName("media_background_color")
    val mediaBackgroundColor: String = "#F1F5F9",

    @SerialName("headline_text_color")
    val headlineTextColor: String = "#6F6F6F",

    @SerialName("body_text_color")
    val bodyTextColor: String = "#8A8A8A",

    @SerialName("cta_background_color")
    val ctaBackgroundColor: String = "#4285F4",

    @SerialName("cta_text_color")
    val ctaTextColor: String = "#FFFFFF",

    @SerialName("ad_attribution_text_color")
    val adAttributionTextColor: String = "#2E7D32",

    @SerialName("ad_attribution_background_color")
    val adAttributionBackgroundColor: String = "#FFFFFF",

    @SerialName("ad_attribution_border_color")
    val adAttributionBorderColor: String = "#2E7D32",

    @SerialName("star_rating_color")
    val starRatingColor: String = "#8DE6DE",

    @SerialName("corner_radius_dp")
    val cornerRadiusDp: Int = 0,

    @SerialName("cta_corner_radius_dp")
    val ctaCornerRadiusDp: Int = 0,

    @SerialName("ad_badge_corner_radius_dp")
    val adBadgeCornerRadiusDp: Int = 5,

    @SerialName("media_corner_radius_dp")
    val mediaCornerRadiusDp: Int = 12,

    @SerialName("placements")
    val placements: Map<String, NativeAdPlacementConfig> = defaultPlacements()
) {
    fun placement(
        key: String
    ): NativeAdPlacementConfig? {
        if (!enabled) return null
        return placements[key]?.takeIf { it.enabled }
    }

    companion object {
        const val TEST_NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

        const val GENERIC = "generic"

        const val ONBOARDING_STEP_1 = "onboarding_step_1"
        const val ONBOARDING_STEP_2 = "onboarding_step_2"
        const val ONBOARDING_STEP_3 = "onboarding_step_3"
        const val ONBOARDING_STEP_4 = "onboarding_step_4"

        const val APP_LANGUAGE_LIST = "app_language_list"

        fun default(): NativeAdConfig = NativeAdConfig()

        fun defaultPlacements(): Map<String, NativeAdPlacementConfig> {
            return mapOf(
                ONBOARDING_STEP_1 to NativeAdPlacementConfig(
                    enabled = true,
                    style = NativeAdStyle.Small,
                    position = NativeAdPosition.Bottom,
                    showPlaceholder = true
                ),
                ONBOARDING_STEP_2 to NativeAdPlacementConfig(
                    enabled = true,
                    style = NativeAdStyle.Small,
                    position = NativeAdPosition.Bottom,
                    showPlaceholder = true
                ),
                ONBOARDING_STEP_3 to NativeAdPlacementConfig(
                    enabled = true,
                    style = NativeAdStyle.Small,
                    position = NativeAdPosition.Top,
                    showPlaceholder = true
                ),
                ONBOARDING_STEP_4 to NativeAdPlacementConfig(
                    enabled = true,
                    style = NativeAdStyle.Large,
                    position = NativeAdPosition.FullPage,
                    showPlaceholder = true
                ),
                APP_LANGUAGE_LIST to NativeAdPlacementConfig(
                    enabled = true,
                    style = NativeAdStyle.Small,
                    position = NativeAdPosition.Bottom,
                    showPlaceholder = true,
                    listInsertionMode = NativeAdListInsertionMode.AfterItem,
                    insertAfterItemIndex = 0
                )
            )
        }
    }
}

@Serializable
data class NativeAdPlacementConfig(
    @SerialName("enabled")
    val enabled: Boolean = true,

    @SerialName("style")
    val style: NativeAdStyle = NativeAdStyle.Small,

    @SerialName("position")
    val position: NativeAdPosition = NativeAdPosition.Bottom,

    @SerialName("show_placeholder")
    val showPlaceholder: Boolean = true,

    @SerialName("list_insertion_mode")
    val listInsertionMode: NativeAdListInsertionMode = NativeAdListInsertionMode.AfterItem,

    @SerialName("insert_after_item_index")
    val insertAfterItemIndex: Int = 2,

    @SerialName("insert_every_n_items")
    val insertEveryNItems: Int = 0
)

@Serializable
enum class NativeAdStyle {
    Small,
    Medium,
    Large
}

@Serializable
enum class NativeAdPosition {
    Top,
    Bottom,
    FullPage
}