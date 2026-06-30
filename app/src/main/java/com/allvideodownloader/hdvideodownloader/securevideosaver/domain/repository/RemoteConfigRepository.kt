package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.AppOpenAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.BannerAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.InterstitialAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import kotlinx.coroutines.flow.StateFlow

interface RemoteConfigRepository {

    val appOpenAdConfig: StateFlow<AppOpenAdConfig>

    val interstitialAdConfig: StateFlow<InterstitialAdConfig>

    val bannerAdConfig: StateFlow<BannerAdConfig>

    val nativeAdConfig: StateFlow<NativeAdConfig>

    suspend fun initializeAndFetch(
        onComplete: () -> Unit
    )

    suspend fun getApiSecretKey(): String?

    suspend fun getBaseUrl(): String?

    fun getCurrentAppOpenAdConfig(): AppOpenAdConfig

    fun getCurrentInterstitialAdConfig(): InterstitialAdConfig

    fun getCurrentBannerAdConfig(): BannerAdConfig
    fun getCurrentNativeAdConfig(): NativeAdConfig

    suspend fun getPremiumIconVisibility(): Boolean

    suspend fun getPrivacyPolicyVisibility(): Boolean

    suspend fun getPrivacyPolicyLink(): String
}