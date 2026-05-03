package com.app.videodownloader.presentation.ads.banner.states

import com.app.videodownloader.domain.model.ads.BannerAdConfig

data class BannerAdState(
    val config: BannerAdConfig = BannerAdConfig.Companion.default()
)