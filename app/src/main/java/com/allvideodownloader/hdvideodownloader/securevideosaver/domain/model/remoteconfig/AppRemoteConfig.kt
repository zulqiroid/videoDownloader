package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.remoteconfig

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AppOpenAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.BannerAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppRemoteConfig(
    @SerialName("ads_app_open_config")
    val appOpenAdConfig: AppOpenAdConfig = AppOpenAdConfig.default(),

    @SerialName("ads_banner_config")
    val bannerAdConfig: BannerAdConfig = BannerAdConfig.default(),

    @SerialName("ads_interstitial_config")
    val interstitialAdConfig: InterstitialAdConfig = InterstitialAdConfig.default(),

    @SerialName("ads_native_config")
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default(),

    @SerialName("api_secret_key_value")
    val apiSecretKeyValue: String = "",

    @SerialName("base_url_remote")
    val baseUrlRemote: String = "",

    @SerialName("privacy_policy_link")
    val privacyPolicyLink: String = "",

    @SerialName("show_premium_icon")
    val showPremiumIcon: Boolean = true,

    @SerialName("show_privacy_policy")
    val showPrivacyPolicy: Boolean = true,
) {
    companion object {
        fun default(): AppRemoteConfig {
            return AppRemoteConfig(
                appOpenAdConfig = AppOpenAdConfig.default(),
                bannerAdConfig = BannerAdConfig.default(),
                interstitialAdConfig = InterstitialAdConfig.default(),
                nativeAdConfig = NativeAdConfig.default(),
                apiSecretKeyValue = "",
                baseUrlRemote = "",
                privacyPolicyLink = "",
                showPremiumIcon = true,
                showPrivacyPolicy = true
            )
        }
    }
}