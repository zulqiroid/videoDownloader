package com.app.videodownloader.domain.usecases.ads

import com.app.videodownloader.data.manager.MobileAdsInitializer


class InitializeMobileAdsUseCase(
    private val mobileAdsInitializer: MobileAdsInitializer
) {
    suspend operator fun invoke(): Boolean {
        return mobileAdsInitializer.initializeIfNeeded()
    }
}