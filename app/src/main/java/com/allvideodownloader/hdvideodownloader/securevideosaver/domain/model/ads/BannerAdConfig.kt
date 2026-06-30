package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BannerAdConfig(
    @SerialName("enabled")
    val enabled: Boolean = true,

    @SerialName("banner_ad_id")
    val adUnitId: String = TEST_BANNER_AD_UNIT_ID,

    @SerialName("screens")
    val screens: Map<String, BannerScreenConfig> = defaultScreens(),

    @SerialName("collapsible_enabled")
    val collapsibleEnabled: Boolean = false,

    @SerialName("collapsible_position")
    val collapsiblePosition: String = COLLAPSIBLE_BOTTOM,
) {

    fun isEnabled(
        screen: BannerAdScreen,
        slot: BannerAdSlot,
    ): Boolean {
        if (!enabled) return false

        val screenConfig = screens[screen.remoteKey] ?: return false

        return when (slot) {
            BannerAdSlot.Top -> screenConfig.top
            BannerAdSlot.Bottom -> screenConfig.bottom
        }
    }

    companion object {
        const val TEST_BANNER_AD_UNIT_ID = ""

        const val COLLAPSIBLE_TOP = "top"
        const val COLLAPSIBLE_BOTTOM = "bottom"

        fun default(): BannerAdConfig {
            return BannerAdConfig()
        }

        fun defaultScreens(): Map<String, BannerScreenConfig> {
            return mapOf(
                BannerAdScreen.Home.remoteKey to BannerScreenConfig(
                    top = true,
                    bottom = true
                ),
                BannerAdScreen.Player.remoteKey to BannerScreenConfig(
                    top = false,
                    bottom = true
                ),
                BannerAdScreen.Download.remoteKey to BannerScreenConfig(
                    top = false,
                    bottom = true
                ),
                BannerAdScreen.Reels.remoteKey to BannerScreenConfig(
                    top = false,
                    bottom = true
                ),
                BannerAdScreen.More.remoteKey to BannerScreenConfig(
                    top = true,
                    bottom = false
                ),
                BannerAdScreen.Social.remoteKey to BannerScreenConfig(
                    top = false,
                    bottom = false
                ),
                BannerAdScreen.MediaPlayer.remoteKey to BannerScreenConfig(
                    top = false,
                    bottom = false
                ),
                BannerAdScreen.AppLanguage.remoteKey to BannerScreenConfig(
                    top = true,
                    bottom = true
                ),
                BannerAdScreen.OnBoarding.remoteKey to BannerScreenConfig(
                    top = true,
                    bottom = true
                ),
                BannerAdScreen.DownloadGuide.remoteKey to BannerScreenConfig(
                    top = true,
                    bottom = true
                ),
            )
        }
    }
}

@Serializable
data class BannerScreenConfig(
    @SerialName("top")
    val top: Boolean = false,

    @SerialName("bottom")
    val bottom: Boolean = false,
)