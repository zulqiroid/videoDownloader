package com.app.videodownloader.presentation.ads.nativeAd

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdPosition
import com.app.videodownloader.domain.model.ads.NativeAdStyle

data class NativeAdLayoutSpec(
    val height: Dp,
    val horizontalPadding: Dp,
    val topSpacing: Dp,
    val bottomSpacing: Dp,
    val isFullPage: Boolean = false
)

object NativeAdLayoutPolicy {

    fun specFor(
        placementKey: String,
        style: NativeAdStyle,
        position: NativeAdPosition
    ): NativeAdLayoutSpec {
        if (position == NativeAdPosition.FullPage || style == NativeAdStyle.Large) {
            return NativeAdLayoutSpec(
                height = 0.dp,
                horizontalPadding = 0.dp,
                topSpacing = 0.dp,
                bottomSpacing = 0.dp,
                isFullPage = true
            )
        }

        return when (placementKey) {
            NativeAdConfig.APP_LANGUAGE_LIST -> {
                NativeAdLayoutSpec(
                    height = 125.dp,
                    horizontalPadding = 20.dp,
                    topSpacing = 8.dp,
                    bottomSpacing = 8.dp
                )
            }

            NativeAdConfig.ONBOARDING_STEP_1,
            NativeAdConfig.ONBOARDING_STEP_2,
            NativeAdConfig.ONBOARDING_STEP_3 -> {
                NativeAdLayoutSpec(
                    height = 125.dp,
                    horizontalPadding = 20.dp,
                    topSpacing = 8.dp,
                    bottomSpacing = 10.dp
                )
            }
            NativeAdConfig.PLAYER_LIST -> {
                playerListSpec(style)
            }
            NativeAdConfig.MORE_TOP,
            NativeAdConfig.MORE_BOTTOM -> {
                moreScreenSpec(style)
            }
            NativeAdConfig.DOWNLOAD_DOWNLOADING_LIST,
            NativeAdConfig.DOWNLOAD_COMPLETED_LIST -> {
                downloadListSpec(style)
            }

            else -> {
                when (style) {
                    NativeAdStyle.Small -> NativeAdLayoutSpec(
                        height = 125.dp,
                        horizontalPadding = 20.dp,
                        topSpacing = 8.dp,
                        bottomSpacing = 8.dp
                    )

                    NativeAdStyle.Medium -> NativeAdLayoutSpec(
                        height = 360.dp,
                        horizontalPadding = 20.dp,
                        topSpacing = 8.dp,
                        bottomSpacing = 8.dp
                    )

                    NativeAdStyle.Large -> NativeAdLayoutSpec(
                        height = 0.dp,
                        horizontalPadding = 0.dp,
                        topSpacing = 0.dp,
                        bottomSpacing = 0.dp,
                        isFullPage = true
                    )
                }
            }
        }
    }

    private fun playerListSpec(
        style: NativeAdStyle
    ): NativeAdLayoutSpec {
        return when (style) {
            NativeAdStyle.Small -> {
                NativeAdLayoutSpec(
                    height = 125.dp,
                    horizontalPadding = 0.dp,
                    topSpacing = 8.dp,
                    bottomSpacing = 8.dp
                )
            }

            NativeAdStyle.Medium -> {
                NativeAdLayoutSpec(
                    height = 360.dp,
                    horizontalPadding = 0.dp,
                    topSpacing = 12.dp,
                    bottomSpacing = 12.dp
                )
            }

            NativeAdStyle.Large -> {
                NativeAdLayoutSpec(
                    height = 0.dp,
                    horizontalPadding = 0.dp,
                    topSpacing = 0.dp,
                    bottomSpacing = 0.dp,
                    isFullPage = true
                )
            }
        }
    }

    private fun moreScreenSpec(
        style: NativeAdStyle
    ): NativeAdLayoutSpec {
        return when (style) {
            NativeAdStyle.Small -> {
                NativeAdLayoutSpec(
                    height = 125.dp,
                    horizontalPadding = 0.dp,
                    topSpacing = 8.dp,
                    bottomSpacing = 8.dp
                )
            }

            NativeAdStyle.Medium -> {
                NativeAdLayoutSpec(
                    height = 360.dp,
                    horizontalPadding = 0.dp,
                    topSpacing = 12.dp,
                    bottomSpacing = 12.dp
                )
            }

            NativeAdStyle.Large -> {
                NativeAdLayoutSpec(
                    height = 0.dp,
                    horizontalPadding = 0.dp,
                    topSpacing = 0.dp,
                    bottomSpacing = 0.dp,
                    isFullPage = true
                )
            }
        }
    }

    private fun downloadListSpec(
        style: NativeAdStyle
    ): NativeAdLayoutSpec {
        return when (style) {
            NativeAdStyle.Small -> {
                NativeAdLayoutSpec(
                    height = 120.dp,
                    horizontalPadding = 16.dp,
                    topSpacing = 8.dp,
                    bottomSpacing = 8.dp
                )
            }

            NativeAdStyle.Medium -> {
                NativeAdLayoutSpec(
                    height = 360.dp,
                    horizontalPadding = 16.dp,
                    topSpacing = 12.dp,
                    bottomSpacing = 12.dp
                )
            }

            NativeAdStyle.Large -> {
                NativeAdLayoutSpec(
                    height = 0.dp,
                    horizontalPadding = 0.dp,
                    topSpacing = 0.dp,
                    bottomSpacing = 0.dp,
                    isFullPage = true
                )
            }
        }
    }
}

