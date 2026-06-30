package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.home.states

import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ReelCategory
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.google.android.gms.ads.nativead.NativeAd

data class HomeState(
    val isLoading: Boolean = false,
    val category: List<ReelCategory> = emptyList(),
    val socialSide: List<SocialSide> = socialSideList(),
    val error: String? = null,
    val url: String = "",


    val isPremiumUser: Boolean = false,
    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default(),
)

data class SocialSide(
    val icon: Int,
    val name: String,
)


fun socialSideList() = listOf(
    SocialSide(
        R.drawable.ic_fabkook,
        "Fabkook"
    ),
    SocialSide(
        R.drawable.ic_instudio,
        "Instudio"
    ),
    SocialSide(
        R.drawable.ic_tickotik,
        "TickoTik"
    ),
    SocialSide(
        R.drawable.ic_twetshot2,
        "Twetsot"
    ),
    SocialSide(
        R.drawable.ic_talktrand,
        "Talktrand"
    ),
    SocialSide(
        R.drawable.ic_dailymoot2,
        "DailyMoots"
    ),
    SocialSide(
        R.drawable.ic_pintests,
        "Pintests"
    ),
    SocialSide(
        R.drawable.ic_liketoo,
        "Liketoo"
    )
)