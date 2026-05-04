package com.app.videodownloader.presentation.screens.onBoarding.componants

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdPosition
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdHost
import com.app.videodownloader.presentation.screens.onBoarding.states.OnboardingPageModel
import com.app.videodownloader.presentation.screens.onBoarding.states.OnboardingPageType
import com.google.android.gms.ads.nativead.NativeAd

@Composable
fun OnboardingPage(
    modifier: Modifier = Modifier,
    model: OnboardingPageModel,
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig
) {
    val placementConfig = model.nativeAdPlacementKey
        ?.let { key -> nativeAdConfig.placement(key) }

    when (model.type) {
        OnboardingPageType.Content -> {
            OnboardingContentPage(
                modifier = modifier,
                model = model,
                nativeAd = nativeAd,
                nativeAdConfig = nativeAdConfig,
                placementConfig = placementConfig
            )
        }

        OnboardingPageType.FullNativeAd -> {
            OnboardingFullNativeAdPage(
                modifier = modifier,
                model = model,
                nativeAd = nativeAd,
                nativeAdConfig = nativeAdConfig,
                placementConfig = placementConfig
            )
        }
    }
}

@Composable
private fun OnboardingContentPage(
    modifier: Modifier,
    model: OnboardingPageModel,
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: com.app.videodownloader.domain.model.ads.NativeAdPlacementConfig?
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (placementConfig?.position == NativeAdPosition.Top) {

            NativeAdHost(
                nativeAd = nativeAd,
                nativeAdConfig = nativeAdConfig,
                placementConfig = placementConfig
            )

       }

        model.imageRes?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = model.title,
                fontSize = 36.sp,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.W800
            )

            Text(
                text = model.highlight,
                fontSize = 36.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFFE00004)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.description,
                textAlign = TextAlign.Center,
                color = Color(0xFF64748B),
                fontSize = 18.sp,
                fontWeight = FontWeight.W400
            )
        }

    }
}

@Composable
private fun OnboardingFullNativeAdPage(
    modifier: Modifier,
    model: OnboardingPageModel,
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: com.app.videodownloader.domain.model.ads.NativeAdPlacementConfig?
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (placementConfig != null) {
            NativeAdHost(
                nativeAd = nativeAd,
                nativeAdConfig = nativeAdConfig,
                placementConfig = placementConfig
            )
        }
    }
}