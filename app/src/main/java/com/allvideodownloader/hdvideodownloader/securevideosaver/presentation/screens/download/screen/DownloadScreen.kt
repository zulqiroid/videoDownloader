package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPlacementConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPosition
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdSlotHelper
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.componants.CompletedCard
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.componants.DownloadTabs
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.componants.DownloadingCard
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.DownloadSearchFilter
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.DownloadSearchUiItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.DownloadState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.DownloadTab
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.DownloadUiItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.currentTabItems
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.searchItems
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.states.toMediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.download.viewModel.DownloadViewModel
import com.google.android.gms.ads.nativead.NativeAd
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel = koinViewModel(),
    searchQuery: String,
    isSearchActive: Boolean,
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaFile) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    if (isSearchActive) {
        DownloadSearchContent(
            state = state,
            query = searchQuery,
            viewModel = viewModel,
            sendToMedia = sendToMedia,
            onMoreClick = onMoreClick
        )
        return
    }

    val list = state.currentTabItems()


    val placementKey = NativeAdConfig.DOWNLOAD_LIST

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
        modifier = Modifier
            .fillMaxSize() ,
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                viewModel = viewModel,
                state = state,
                sendToMedia = sendToMedia,
                onMoreClick = onMoreClick
            )
        }
    }
}


@Composable
private fun DownloadListContent(
    selectedTab: DownloadTab,
    list: List<DownloadUiItem>,
    state: DownloadState,
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
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        itemsIndexed(
            items = list,
            key = { _, item -> item.id }
        ) { index, item ->

            when (selectedTab) {
                DownloadTab.DOWNLOADING -> {
                    DownloadingCard(
                        item = item,
                        state = state,
                        viewModel = viewModel
                    )
                }

                DownloadTab.COMPLETED -> {
                    CompletedCard(
                        item = item,
                        state = state,
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

        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DownloadEmptyContent(
    selectedTab: DownloadTab,
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


@Composable
private fun DownloadSearchContent(
    state: DownloadState,
    query: String,
    viewModel: DownloadViewModel,
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaFile) -> Unit,
) {
    var selectedFilter by remember {
        mutableStateOf(DownloadSearchFilter.ALL)
    }

    val searchResults = state.searchItems(
        query = query,
        filter = selectedFilter
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 16.dp)
    ) {
        DownloadSearchFilterTabs(
            selectedFilter = selectedFilter,
            onFilterSelected = {
                selectedFilter = it
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (query.isBlank()) {
                "${searchResults.size} results found"
            } else {
                "${searchResults.size} results found for \"$query\""
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (searchResults.isEmpty()) {
            DownloadSearchEmptyContent()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(
                    items = searchResults,
                    key = { _, searchItem ->
                        "${searchItem.tab}_${searchItem.item.id}"
                    }
                ) { _, searchItem ->
                    DownloadSearchResultItem(
                        searchItem = searchItem,
                        state = state,
                        viewModel = viewModel,
                        sendToMedia = sendToMedia,
                        onMoreClick = onMoreClick,
                        allSearchResults = searchResults
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun DownloadSearchFilterTabs(
    selectedFilter: DownloadSearchFilter,
    onFilterSelected: (DownloadSearchFilter) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DownloadSearchFilterChip(
            text = "All",
            selected = selectedFilter == DownloadSearchFilter.ALL,
            onClick = {
                onFilterSelected(DownloadSearchFilter.ALL)
            }
        )

        DownloadSearchFilterChip(
            text = "Downloading",
            selected = selectedFilter == DownloadSearchFilter.DOWNLOADING,
            onClick = {
                onFilterSelected(DownloadSearchFilter.DOWNLOADING)
            }
        )

        DownloadSearchFilterChip(
            text = "Completed",
            selected = selectedFilter == DownloadSearchFilter.COMPLETED,
            onClick = {
                onFilterSelected(DownloadSearchFilter.COMPLETED)
            }
        )
    }
}

@Composable
private fun DownloadSearchFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (selected) {
                    Color(0xFFE00004)
                } else {
                    Color(0xFFEDEFF2)
                }
            )
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 34.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color(0xFF111827),
            fontSize = 12.sp,
            fontWeight = FontWeight.W700
        )
    }
}

@Composable
private fun DownloadSearchResultItem(
    searchItem: DownloadSearchUiItem,
    state: DownloadState,
    viewModel: DownloadViewModel,
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaFile) -> Unit,
    allSearchResults: List<DownloadSearchUiItem>,
) {
    when (searchItem.tab) {
        DownloadTab.DOWNLOADING -> {
            DownloadingCard(
                item = searchItem.item,
                state = state,
                viewModel = viewModel
            )
        }

        DownloadTab.COMPLETED -> {
            CompletedCard(
                item = searchItem.item,
                state = state,
                viewModel = viewModel,
                onItemCLicked = {
                    val completedResults = allSearchResults.filter {
                        it.tab == DownloadTab.COMPLETED
                    }

                    val mediaList = completedResults.map {
                        it.item.toMediaFile(true)
                    }

                    val startIndex = completedResults.indexOfFirst {
                        it.item.id == searchItem.item.id
                    }.coerceAtLeast(0)

                    if (mediaList.isNotEmpty()) {
                        sendToMedia(
                            mediaList,
                            startIndex
                        )
                    }
                },
                onMoreClicked = {
                    onMoreClick(
                        it.toMediaFile(true)
                    )
                }
            )
        }
    }
}

@Composable
private fun DownloadSearchEmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No downloads found",
                color = Color(0xFF111827),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Try searching with another file name.",
                color = Color(0xFF6B7280),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}