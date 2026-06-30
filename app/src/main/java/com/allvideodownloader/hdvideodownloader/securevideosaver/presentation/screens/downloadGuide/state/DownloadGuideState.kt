package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.state


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.google.android.gms.ads.nativead.NativeAd

data class DownloadGuideState(
    val steps: List<GuideStep> = emptyList(),

    val isPremiumUser: Boolean = false,
    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default(),
)




data class GuideStep(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val icon: Int
)