package com.app.videodownloader.domain.repository

import com.app.videodownloader.domain.model.ApiKey
import com.app.videodownloader.domain.model.ads.AppOpenAdConfig
import com.app.videodownloader.domain.model.ads.BannerAdConfig
import com.app.videodownloader.domain.model.ads.InterstitialAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import kotlinx.coroutines.flow.StateFlow

interface RemoteConfigRepository {

    val appOpenAdConfig: StateFlow<AppOpenAdConfig>

    val interstitialAdConfig: StateFlow<InterstitialAdConfig>

    val bannerAdConfig: StateFlow<BannerAdConfig>

    val nativeAdConfig: StateFlow<NativeAdConfig>

    suspend fun initializeAndFetch(
        onComplete: () -> Unit
    )

    suspend fun getApiSecretKey(): ApiKey

    suspend fun getBaseUrl(): String

    fun getCurrentAppOpenAdConfig(): AppOpenAdConfig

    fun getCurrentInterstitialAdConfig(): InterstitialAdConfig

    fun getCurrentBannerAdConfig(): BannerAdConfig
    fun getCurrentNativeAdConfig(): NativeAdConfig
}