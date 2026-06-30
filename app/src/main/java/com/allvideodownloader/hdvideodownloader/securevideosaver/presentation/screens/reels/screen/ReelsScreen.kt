package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.Reel
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPosition
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.componants.ReelItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.events.ReelsEvent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.reels.viewModel.ReelsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelsScreen(
    selectedReel: Reel? = null,
    viewModel: ReelsViewModel = koinViewModel(),
    onDownloadClick: (String) -> Unit,
    onShareClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = state.currentIndex,
        pageCount = { state.reels.size }
    )

    LaunchedEffect(Unit) {
        viewModel.onEvent(ReelsEvent.LoadReels)
    }

    LaunchedEffect(state.reels, selectedReel) {
        viewModel.setCurrentIndex(selectedReel)

        val targetIndex = state.reels.indexOfFirst { reel ->
            reel.id == selectedReel?.id
        }

        if (targetIndex != -1 && targetIndex != pagerState.currentPage) {
            pagerState.scrollToPage(targetIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(
            ReelsEvent.OnPageChanged(pagerState.currentPage)
        )
    }

    val placementKey = NativeAdConfig.REELS

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
    ) { paddingValues ->

        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val reel = state.reels[page]

                ReelItem(
                    reel = reel,
                    isActive = page == state.currentIndex,
                    onLikeClick = {
                        viewModel.onEvent(
                            ReelsEvent.OnLikeClicked(reel.id)
                        )
                    },
                    onShareClick = {
                        onShareClick(reel.videoUrl)
                    },
                    onDownloadClick = {
                        onDownloadClick(reel.videoUrl)
                    }
                )
            }
        }
    }
}