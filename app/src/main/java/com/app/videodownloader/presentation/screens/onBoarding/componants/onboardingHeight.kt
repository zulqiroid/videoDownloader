package com.app.videodownloader.presentation.screens.onBoarding.componants

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.videodownloader.domain.model.ads.NativeAdStyle

fun NativeAdStyle.onboardingHeight(): Dp {
    return when (this) {
        NativeAdStyle.Small -> 80.dp
        NativeAdStyle.Medium -> 120.dp
        NativeAdStyle.Large -> 320.dp
    }
}