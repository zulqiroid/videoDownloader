package com.app.videodownloader.presentation.ads.nativeAd

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdPlacementConfig
import com.app.videodownloader.domain.usecases.ads.CanRequestAdsUseCase
import com.app.videodownloader.presentation.ads.nativeAd.componants.NativeAdPlaceholder
import com.google.android.gms.ads.nativead.NativeAd
import org.koin.compose.koinInject

@Composable
fun NativeAdHost(
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: NativeAdPlacementConfig,
    modifier: Modifier = Modifier,
    placementKey: String = NativeAdConfig.GENERIC
) {
    if (!nativeAdConfig.enabled || !placementConfig.enabled) {
        return
    }

    val canRequestAdsUseCase: CanRequestAdsUseCase = koinInject()

    if (!canRequestAdsUseCase()) {
        return
    }

    val layoutSpec = remember(
        placementKey,
        placementConfig.style,
        placementConfig.position
    ) {
        NativeAdLayoutPolicy.specFor(
            placementKey = placementKey,
            style = placementConfig.style,
            position = placementConfig.position
        )
    }

    key(
        placementKey,
        placementConfig.style,
        placementConfig.position
    ) {
        if (layoutSpec.isFullPage) {
            FullPageNativeAdContent(
                nativeAd = nativeAd,
                nativeAdConfig = nativeAdConfig,
                placementConfig = placementConfig,
                modifier = modifier
            )
        } else {
            InlineNativeAdContent(
                nativeAd = nativeAd,
                nativeAdConfig = nativeAdConfig,
                placementConfig = placementConfig,
                layoutSpec = layoutSpec,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun FullPageNativeAdContent(
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: NativeAdPlacementConfig,
    modifier: Modifier = Modifier
) {
    if (nativeAd != null) {
        NativeAdCard(
            nativeAd = nativeAd,
            config = nativeAdConfig,
            style = placementConfig.style,
            modifier = modifier.fillMaxSize()
        )
    } else if (placementConfig.showPlaceholder) {
        NativeAdPlaceholder(
            style = placementConfig.style,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
private fun InlineNativeAdContent(
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: NativeAdPlacementConfig,
    layoutSpec: NativeAdLayoutSpec,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(
            modifier = Modifier.height(layoutSpec.topSpacing)
        )

        val adModifier = Modifier
            .fillMaxWidth()
            .height(layoutSpec.height)
            .padding(horizontal = layoutSpec.horizontalPadding)

        if (nativeAd != null) {
            NativeAdCard(
                nativeAd = nativeAd,
                config = nativeAdConfig,
                style = placementConfig.style,
                modifier = adModifier
            )
        } else if (placementConfig.showPlaceholder) {
            NativeAdPlaceholder(
                style = placementConfig.style,
                modifier = adModifier
            )
        }

        Spacer(
            modifier = Modifier.height(layoutSpec.bottomSpacing)
        )
    }
}