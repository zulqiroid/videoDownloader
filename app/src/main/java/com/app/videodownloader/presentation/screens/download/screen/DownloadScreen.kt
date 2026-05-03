package com.app.videodownloader.presentation.screens.download.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdPlacementConfig
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdHost
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdListHelper
import com.app.videodownloader.presentation.screens.download.componants.CompletedCard
import com.app.videodownloader.presentation.screens.download.componants.DownloadTabs
import com.app.videodownloader.presentation.screens.download.componants.DownloadingCard
import com.app.videodownloader.presentation.screens.download.states.DownloadTab
import com.app.videodownloader.presentation.screens.download.states.DownloadUiItem
import com.app.videodownloader.presentation.screens.download.states.toMediaFile
import com.app.videodownloader.presentation.screens.download.viewModel.DownloadViewModel
import com.google.android.gms.ads.nativead.NativeAd
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel = koinViewModel(),
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaFile) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val list = when (state.selectedTab) {
        DownloadTab.DOWNLOADING -> state.downloading
        DownloadTab.COMPLETED -> state.completed
    }

    val nativePlacementKey = when (state.selectedTab) {
        DownloadTab.DOWNLOADING -> NativeAdConfig.DOWNLOAD_DOWNLOADING_LIST
        DownloadTab.COMPLETED -> NativeAdConfig.DOWNLOAD_COMPLETED_LIST
    }

    val nativePlacementConfig = state.nativeAdConfig.placement(nativePlacementKey)
    val nativeAd = state.nativeAds[nativePlacementKey]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        DownloadTabs(
            selected = state.selectedTab,
            downloadingCount = state.downloading.size,
            completedCount = state.completed.size,
            onTabSelected = {
                viewModel.updateTab(it)
            }
        )

        DownloadListContent(
            selectedTab = state.selectedTab,
            list = list,
            nativeAd = nativeAd,
            nativeAdConfig = state.nativeAdConfig,
            nativePlacementConfig = nativePlacementConfig,
            nativePlacementKey = nativePlacementKey,
            viewModel = viewModel,
            sendToMedia = sendToMedia,
            onMoreClick = onMoreClick
        )
    }
}

@Composable
private fun DownloadListContent(
    selectedTab: DownloadTab,
    list: List<DownloadUiItem>,
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    nativePlacementConfig: NativeAdPlacementConfig?,
    nativePlacementKey: String,
    viewModel: DownloadViewModel,
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaFile) -> Unit,
) {
    if (list.isEmpty()) {
        DownloadEmptyContent(
            selectedTab = selectedTab,
            nativeAd = nativeAd,
            nativeAdConfig = nativeAdConfig,
            nativePlacementConfig = nativePlacementConfig,
            nativePlacementKey = nativePlacementKey
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        if (
            nativePlacementConfig != null &&
            NativeAdListHelper.shouldShowAdAfterItem(
                index = -1,
                totalItems = list.size,
                config = nativePlacementConfig
            )
        ) {
            item(key = "download_native_ad_start_$nativePlacementKey") {
                DownloadNativeAdItem(
                    nativeAd = nativeAd,
                    nativeAdConfig = nativeAdConfig,
                    placementConfig = nativePlacementConfig,
                    placementKey = nativePlacementKey
                )
            }
        }

        itemsIndexed(
            items = list,
            key = { _, item -> item.id }
        ) { index, item ->

            if (selectedTab == DownloadTab.DOWNLOADING) {
                DownloadingCard(
                    item = item,
                    state = viewModel.state.value,
                    viewModel = viewModel
                )
            } else {
                CompletedCard(
                    item = item,
                    state = viewModel.state.value,
                    viewModel = viewModel,
                    onItemCLicked = {
                        sendToMedia(
                            list.map { it.toMediaFile(true) },
                            index
                        )
                    },
                    onMoreClicked = {
                        onMoreClick(
                            it.toMediaFile(true)
                        )
                    }
                )
            }

            if (
                nativePlacementConfig != null &&
                NativeAdListHelper.shouldShowAdAfterItem(
                    index = index,
                    totalItems = list.size,
                    config = nativePlacementConfig
                )
            ) {
                DownloadNativeAdItem(
                    nativeAd = nativeAd,
                    nativeAdConfig = nativeAdConfig,
                    placementConfig = nativePlacementConfig,
                    placementKey = nativePlacementKey
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DownloadEmptyContent(
    selectedTab: DownloadTab,
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    nativePlacementConfig: NativeAdPlacementConfig?,
    nativePlacementKey: String
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (selectedTab) {
                        DownloadTab.DOWNLOADING -> "No active downloads"
                        DownloadTab.COMPLETED -> "No completed downloads"
                    },
                    color = Color(0xFF0F172A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when (selectedTab) {
                        DownloadTab.DOWNLOADING -> "Your active downloads will appear here."
                        DownloadTab.COMPLETED -> "Downloaded videos will appear here."
                    },
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (nativePlacementConfig != null) {
            DownloadNativeAdItem(
                nativeAd = nativeAd,
                nativeAdConfig = nativeAdConfig,
                placementConfig = nativePlacementConfig,
                placementKey = nativePlacementKey
            )
        }
    }
}

@Composable
private fun DownloadNativeAdItem(
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: NativeAdPlacementConfig,
    placementKey: String
) {
    NativeAdHost(
        nativeAd = nativeAd,
        nativeAdConfig = nativeAdConfig,
        placementConfig = placementConfig,
        placementKey = placementKey
    )
}