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
                    height = 145.dp,
                    horizontalPadding = 20.dp,
                    topSpacing = 8.dp,
                    bottomSpacing = 8.dp
                )
            }

            NativeAdConfig.ONBOARDING_STEP_1,
            NativeAdConfig.ONBOARDING_STEP_2,
            NativeAdConfig.ONBOARDING_STEP_3 -> {
                NativeAdLayoutSpec(
                    height = 126.dp,
                    horizontalPadding = 20.dp,
                    topSpacing = 8.dp,
                    bottomSpacing = 10.dp
                )
            }

            else -> {
                when (style) {
                    NativeAdStyle.Small -> NativeAdLayoutSpec(
                        height = 120.dp,
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
}