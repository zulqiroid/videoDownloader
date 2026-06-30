package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdListInsertionMode
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPlacementConfig

object NativeAdSlotHelper {

    fun slotKeyForStart(): String {
        return "start"
    }

    fun slotKeyForEnd(): String {
        return "end"
    }

    fun slotKeyAfterItem(
        index: Int
    ): String {
        return "after_$index"
    }

    fun insertionSlotKeys(
        totalItems: Int,
        config: NativeAdPlacementConfig
    ): List<String> {
        if (!config.enabled) return emptyList()

        return when (config.listInsertionMode) {
            NativeAdListInsertionMode.Disabled -> {
                emptyList()
            }

            NativeAdListInsertionMode.Start -> {
                listOf(slotKeyForStart())
            }

            NativeAdListInsertionMode.End -> {
                listOf(slotKeyForEnd())
            }

            NativeAdListInsertionMode.AfterItem -> {
                if (totalItems <= 0) {
                    emptyList()
                } else {
                    listOf(
                        slotKeyAfterItem(
                            config.insertAfterItemIndex.coerceIn(
                                0,
                                totalItems - 1
                            )
                        )
                    )
                }
            }

            NativeAdListInsertionMode.EveryNItems -> {
                if (totalItems <= 0 || config.insertEveryNItems <= 0) {
                    emptyList()
                } else {
                    buildList {
                        for (index in 0 until totalItems) {
                            val shouldInsert =
                                (index + 1) % config.insertEveryNItems == 0

                            if (shouldInsert) {
                                add(slotKeyAfterItem(index))
                            }
                        }
                    }
                }
            }
        }
    }

    fun slotKeyForIndex(
        index: Int,
        totalItems: Int,
        config: NativeAdPlacementConfig
    ): String? {
        if (!config.enabled) return null

        return when (config.listInsertionMode) {
            NativeAdListInsertionMode.Disabled -> null

            NativeAdListInsertionMode.Start -> {
                if (index == -1) slotKeyForStart() else null
            }

            NativeAdListInsertionMode.End -> {
                if (index == totalItems - 1) slotKeyForEnd() else null
            }

            NativeAdListInsertionMode.AfterItem -> {
                val safeIndex = config.insertAfterItemIndex.coerceIn(
                    0,
                    (totalItems - 1).coerceAtLeast(0)
                )

                if (index == safeIndex) slotKeyAfterItem(index) else null
            }

            NativeAdListInsertionMode.EveryNItems -> {
                if (
                    totalItems > 0 &&
                    config.insertEveryNItems > 0 &&
                    index >= 0 &&
                    (index + 1) % config.insertEveryNItems == 0
                ) {
                    slotKeyAfterItem(index)
                } else {
                    null
                }
            }
        }
    }
}