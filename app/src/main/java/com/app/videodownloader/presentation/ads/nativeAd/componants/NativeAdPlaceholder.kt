package com.app.videodownloader.presentation.ads.nativeAd.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.videodownloader.domain.model.ads.NativeAdStyle

@Composable
fun NativeAdPlaceholder(
    style: NativeAdStyle,
    modifier: Modifier = Modifier
) {
    /*
     * Do not force height here.
     *
     * NativeAdHost already applies the correct Remote Config height.
     * This placeholder should only fill the space provided by the parent.
     */
    Box(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (style) {
                NativeAdStyle.Small -> "Advertisement"
                NativeAdStyle.Medium -> "Advertisement"
                NativeAdStyle.Large -> "Sponsored"
            },
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.W500
        )
    }
}