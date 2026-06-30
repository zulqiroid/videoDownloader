package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.home.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.Reel
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.SocialPlatform
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPosition
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.ReelGrid
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.SectionHeader
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.TopSearchBar
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.home.componants.SocialAppsRow
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.home.events.HomeEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.home.viewModel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.layout.WindowInsets

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    fetchUrl: (String) -> Unit,
    downloadReel: (Reel) -> Unit,
    playReel: (Reel) -> Unit,
    onReelSeeAllCLicked: () -> Unit,
    onSocialClick: (SocialPlatform) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvents.LoadReels)
    }

    val placementKey = NativeAdConfig.HOME

    val canShowAds = !state.isPremiumUser

    val placementConfig = if (canShowAds) {
        state.nativeAdConfig.placement(placementKey)
    } else {
        null
    }

    val nativeAd = if (canShowAds) {
        state.nativeAds[placementKey]
    } else {
        null
    }

    val showTopNativeAd =
        placementConfig?.position == NativeAdPosition.Top &&
                !state.isPremiumUser &&
                nativeAd != null

    val showBottomNativeAd =
        placementConfig?.position == NativeAdPosition.Bottom &&
                !state.isPremiumUser &&
                nativeAd != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp,0.dp),
        topBar = if (showTopNativeAd) {
            {
                NativeAdHost(
                    nativeAd = nativeAd,
                    nativeAdConfig = state.nativeAdConfig,
                    placementConfig = placementConfig,
                    placementKey = placementKey
                )
            }
        } else {
            {}
        },
        bottomBar = if (showBottomNativeAd) {
            {
                Column {
                    NativeAdHost(
                        nativeAd = nativeAd,
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = placementConfig,
                        placementKey = placementKey
                    )

                    Spacer(
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        } else {
            {}
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            TopSearchBar(
                value = state.url,
                onValueChange = { value ->
                    viewModel.onEvent(HomeEvents.OnUrlChange(value))
                },
                onDownloadClick = {
                    if (state.url.isNotBlank()) {
                        fetchUrl(state.url)
                    }
                }
            )

            Spacer(
                modifier = Modifier.size(20.dp)
            )

            SocialAppsRow(
                state = state,
                onSocialClick = onSocialClick
            )

            SectionHeader(
                textRes = R.string.home_trending_reels_title,
                onReelSeeAllCLicked = {
                    onReelSeeAllCLicked()
                }
            )

            ReelGrid(
                categories = state.category,
                onDownloadClick = downloadReel,
                onPlayClick = playReel
            )
        }
    }
}