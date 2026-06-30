package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.states

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.google.android.gms.ads.nativead.NativeAd

data class ReelsState(
    val reels: List<ReelUi> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
    val likedReelIds: Set<String> = emptySet(),

    val isPremiumUser: Boolean = false,
    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default(),
)

data class ReelUi(
    val id: String,
    val videoUrl: String,
    val username: String,
    val caption: String,
    val isLiked: Boolean = false,
    val likeCount: Int = 105
)