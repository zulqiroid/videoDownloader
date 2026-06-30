package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.ads

import com.allvideodownloader.hdvideodownloader.securevideosaver.data.manager.MobileAdsInitializer


class InitializeMobileAdsUseCase(
    private val mobileAdsInitializer: MobileAdsInitializer
) {
    suspend operator fun invoke(): Boolean {
        return mobileAdsInitializer.initializeIfNeeded()
    }
}