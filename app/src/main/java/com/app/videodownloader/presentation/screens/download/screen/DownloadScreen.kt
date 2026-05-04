package com.app.videodownloader.presentation.screens.download.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.ads.BannerAdScreen
import com.app.videodownloader.domain.model.ads.BannerAdSlot
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdPlacementConfig
import com.app.videodownloader.presentation.ads.banner.componants.BannerAdHost
import com.app.videodownloader.presentation.ads.banner.viewModel.BannerAdViewModel
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdHost
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdSlotHelper
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
    val nativeAdPool = state.nativeAdPools[nativePlacementKey].orEmpty()


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
         ) {
            DownloadTabs(
                selected = state.selectedTab,
                downloadingCount = state.downloading.size,
                completedCount = state.completed.size,
                onTabSelected = viewModel::updateTab
            )

            DownloadListContent(
                selectedTab = state.selectedTab,
                list = list,
                nativeAdPool = nativeAdPool,
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
    nativeAdPool: Map<String, NativeAd>,
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
            nativeAd = nativeAdPool[DownloadViewModel.EMPTY_STATE_SLOT_KEY],
            nativeAdConfig = nativeAdConfig,
            nativePlacementConfig = nativePlacementConfig,
            nativePlacementKey = nativePlacementKey,
            slotKey = DownloadViewModel.EMPTY_STATE_SLOT_KEY
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        val startSlotKey = nativePlacementConfig?.let { config ->
            NativeAdSlotHelper.slotKeyForIndex(
                index = -1,
                totalItems = list.size,
                config = config
            )
        }

        if (nativePlacementConfig != null && startSlotKey != null) {
            item(key = "download_native_ad_${selectedTab}_$startSlotKey") {
                DownloadNativeAdItem(
                    nativeAd = nativeAdPool[startSlotKey],
                    nativeAdConfig = nativeAdConfig,
                    placementConfig = nativePlacementConfig,
                    placementKey = nativePlacementKey,
                    slotKey = startSlotKey
                )
            }
        }

        itemsIndexed(
            items = list,
            key = { _, item -> item.id }
        ) { index, item ->

            when (selectedTab) {
                DownloadTab.DOWNLOADING -> {
                    DownloadingCard(
                        item = item,
                        state = viewModel.state.value,
                        viewModel = viewModel
                    )
                }

                DownloadTab.COMPLETED -> {
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
            }

            val slotKey = nativePlacementConfig?.let { config ->
                NativeAdSlotHelper.slotKeyForIndex(
                    index = index,
                    totalItems = list.size,
                    config = config
                )
            }

            if (nativePlacementConfig != null && slotKey != null) {
                DownloadNativeAdItem(
                    nativeAd = nativeAdPool[slotKey],
                    nativeAdConfig = nativeAdConfig,
                    placementConfig = nativePlacementConfig,
                    placementKey = nativePlacementKey,
                    slotKey = slotKey
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
    nativePlacementKey: String,
    slotKey: String,
) {
    val title = when (selectedTab) {
        DownloadTab.DOWNLOADING -> stringResource(R.string.download_empty_active_title)
        DownloadTab.COMPLETED -> stringResource(R.string.download_empty_completed_title)
    }

    val message = when (selectedTab) {
        DownloadTab.DOWNLOADING -> stringResource(R.string.download_empty_active_message)
        DownloadTab.COMPLETED -> stringResource(R.string.download_empty_completed_message)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (nativePlacementConfig != null) {
            DownloadNativeAdItem(
                nativeAd = nativeAd,
                nativeAdConfig = nativeAdConfig,
                placementConfig = nativePlacementConfig,
                placementKey = nativePlacementKey,
                slotKey = slotKey
            )
        }
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
                    text = title,
                    color = Color(0xFF0F172A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }


    }
}

@Composable
private fun DownloadNativeAdItem(
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: NativeAdPlacementConfig,
    placementKey: String,
    slotKey: String,
) {
    NativeAdHost(
        nativeAd = nativeAd,
        nativeAdConfig = nativeAdConfig,
        placementConfig = placementConfig,
        placementKey = "${placementKey}_$slotKey"
    )
}