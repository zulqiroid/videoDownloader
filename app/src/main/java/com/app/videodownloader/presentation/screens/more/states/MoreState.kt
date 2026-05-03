package com.app.videodownloader.presentation.screens.more.states

import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.google.android.gms.ads.nativead.NativeAd

data class MoreState(
    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default()
)