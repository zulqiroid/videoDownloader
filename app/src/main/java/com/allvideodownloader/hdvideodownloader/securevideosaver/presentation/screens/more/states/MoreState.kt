package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.more.states

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.google.android.gms.ads.nativead.NativeAd

data class MoreState(
    val isPremiumUser: Boolean = false,
    val isPremiumvisible: Boolean = false,
    val isPrivacyPolicyVisible : Boolean = true,
    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default()
)