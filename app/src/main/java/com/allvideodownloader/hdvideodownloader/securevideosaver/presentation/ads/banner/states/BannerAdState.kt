package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.banner.states

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.BannerAdConfig

data class BannerAdState(
    val config: BannerAdConfig = BannerAdConfig.Companion.default()
)