package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdListInsertionMode
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPlacementConfig

object NativeAdListHelper {

    fun shouldShowAdAfterItem(
        index: Int,
        totalItems: Int,
        config: NativeAdPlacementConfig
    ): Boolean {
        if (!config.enabled) return false

        return when (config.listInsertionMode) {
            NativeAdListInsertionMode.Disabled -> false

            NativeAdListInsertionMode.Start -> index == -1

            NativeAdListInsertionMode.End -> index == totalItems - 1

            NativeAdListInsertionMode.AfterItem -> {
                index == config.insertAfterItemIndex.coerceAtLeast(0)
            }

            NativeAdListInsertionMode.EveryNItems -> {
                config.insertEveryNItems > 0 &&
                        (index + 1) % config.insertEveryNItems == 0
            }
        }
    }
}