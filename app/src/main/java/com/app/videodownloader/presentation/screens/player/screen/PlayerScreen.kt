package com.app.videodownloader.presentation.screens.player.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdPlacementConfig
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdHost
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdListHelper
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdSlotHelper
import com.app.videodownloader.presentation.screens.download.componants.VideoThumbnail
import com.app.videodownloader.presentation.screens.player.componants.MediaTabs
import com.app.videodownloader.presentation.screens.player.componants.PlayAllButton
import com.app.videodownloader.presentation.screens.player.states.PlayerState
import com.app.videodownloader.presentation.screens.player.states.PlayerTab
import com.app.videodownloader.presentation.screens.player.states.PlayerUiItem
import com.app.videodownloader.presentation.screens.player.states.toMediaFile
import com.app.videodownloader.presentation.screens.player.viewModel.PlayerViewModel
import com.google.android.gms.ads.nativead.NativeAd
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdHost
import com.app.videodownloader.presentation.screens.download.componants.VideoThumbnail
import com.app.videodownloader.presentation.screens.player.componants.MediaTabs
import com.app.videodownloader.presentation.screens.player.componants.PlayAllButton
import com.app.videodownloader.presentation.screens.player.states.PlayerSearchFilter
import com.app.videodownloader.presentation.screens.player.states.PlayerSearchUiItem
import com.app.videodownloader.presentation.screens.player.states.currentTabItems
import com.app.videodownloader.presentation.screens.player.states.searchItems
import com.app.videodownloader.presentation.screens.player.states.toMediaFile
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = koinViewModel(),
    hasMediaPermission: Boolean,
    searchQuery: String,
    isSearchActive: Boolean,
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaFile) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(hasMediaPermission) {
        if (hasMediaPermission) {
            viewModel.onMediaPermissionGranted()
        } else {
            viewModel.onMediaPermissionDenied()
        }
    }

    if (isSearchActive) {
        PlayerSearchContent(
            state = state,
            query = searchQuery,
            sendToMedia = sendToMedia,
            onMoreClick = onMoreClick
        )
    } else {
        PlayerDefaultContent(
            state = state,
            viewModel = viewModel,
            sendToMedia = sendToMedia,
            onMoreClick = onMoreClick
        )
    }
}

@Composable
private fun PlayerDefaultContent(
    state: PlayerState,
    viewModel: PlayerViewModel,
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaFile) -> Unit,
) {
    val list = state.currentTabItems()

    val nativePlacementKey = NativeAdConfig.PLAYER_LIST
    val canShowNativeAds = !state.isPremiumUser

    val nativePlacementConfig = if (canShowNativeAds) {
        state.nativeAdConfig.placement(nativePlacementKey)
    } else {
        null
    }

    val nativeAdPool = if (canShowNativeAds) {
        state.nativeAdPools[nativePlacementKey].orEmpty()
    } else {
        emptyMap()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        MediaTabs(
            selectedTab = state.selectedTab,
            onTabChange = viewModel::onTabChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        PlayAllButton(
            state = state,
            viewModel = viewModel,
            onClick = {
                val isVideo = state.selectedTab == PlayerTab.VIDEO
                val mediaList = list.map { it.toMediaFile(isVideo) }

                if (mediaList.isNotEmpty()) {
                    sendToMedia(mediaList, 0)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val startSlotKey = nativePlacementConfig?.let { config ->
                NativeAdSlotHelper.slotKeyForIndex(
                    index = -1,
                    totalItems = list.size,
                    config = config
                )
            }

            if (nativePlacementConfig != null && startSlotKey != null) {
                item(key = "player_native_ad_${state.selectedTab}_$startSlotKey") {
                    PlayerNativeAdItem(
                        nativeAd = nativeAdPool[startSlotKey],
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = nativePlacementConfig,
                        slotKey = startSlotKey
                    )
                }
            }

            itemsIndexed(
                items = list,
                key = { _, item -> item.id }
            ) { index, item ->
                val isVideo = state.selectedTab == PlayerTab.VIDEO

                if (isVideo) {
                    VideoCard(
                        item = item,
                        onClick = {
                            val mediaList = state.videos.map { it.toMediaFile(true) }

                            if (mediaList.isNotEmpty()) {
                                sendToMedia(mediaList, index)
                            }
                        },
                        onMoreClick = {
                            onMoreClick(it.toMediaFile(true))
                        }
                    )
                } else {
                    AudioCard(
                        item = item,
                        onClick = {
                            val mediaList = state.audios.map { it.toMediaFile(false) }

                            if (mediaList.isNotEmpty()) {
                                sendToMedia(mediaList, index)
                            }
                        },
                        onMoreClick = {
                            onMoreClick(it.toMediaFile(false))
                        }
                    )
                }

                val slotKey = nativePlacementConfig?.let { config ->
                    NativeAdSlotHelper.slotKeyForIndex(
                        index = index,
                        totalItems = list.size,
                        config = config
                    )
                }

                if (nativePlacementConfig != null && slotKey != null) {
                    PlayerNativeAdItem(
                        nativeAd = nativeAdPool[slotKey],
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = nativePlacementConfig,
                        slotKey = slotKey
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun PlayerSearchContent(
    state: PlayerState,
    query: String,
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaFile) -> Unit,
) {
    var selectedFilter by remember {
        mutableStateOf(PlayerSearchFilter.ALL_TYPES)
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
        PlayerSearchFilterTabs(
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(
                items = searchResults,
                key = { _, searchItem ->
                    "${searchItem.item.id}_${searchItem.isVideo}"
                }
            ) { index, searchItem ->
                PlayerSearchResultCard(
                    searchItem = searchItem,
                    onClick = {
                        val sameTypeResults = searchResults.filter {
                            it.isVideo == searchItem.isVideo
                        }

                        val mediaList = sameTypeResults.map {
                            it.item.toMediaFile(it.isVideo)
                        }

                        val startIndex = sameTypeResults.indexOfFirst {
                            it.item.id == searchItem.item.id
                        }.coerceAtLeast(0)

                        if (mediaList.isNotEmpty()) {
                            sendToMedia(mediaList, startIndex)
                        }
                    },
                    onMoreClick = {
                        onMoreClick(
                            searchItem.item.toMediaFile(searchItem.isVideo)
                        )
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun PlayerSearchFilterTabs(
    selectedFilter: PlayerSearchFilter,
    onFilterSelected: (PlayerSearchFilter) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerSearchFilterChip(
            text = "All Types",
            selected = selectedFilter == PlayerSearchFilter.ALL_TYPES,
            icon = null,
            onClick = {
                onFilterSelected(PlayerSearchFilter.ALL_TYPES)
            }
        )

        PlayerSearchFilterChip(
            text = "Video",
            selected = selectedFilter == PlayerSearchFilter.VIDEO,
            icon = R.drawable.ic_vd_bundle_outlined,
            onClick = {
                onFilterSelected(PlayerSearchFilter.VIDEO)
            }
        )

        PlayerSearchFilterChip(
            text = "Audio",
            selected = selectedFilter == PlayerSearchFilter.AUDIO,
            icon = R.drawable.ic_music_outlined,
            onClick = {
                onFilterSelected(PlayerSearchFilter.AUDIO)
            }
        )
    }
}

@Composable
private fun PlayerSearchFilterChip(
    text: String,
    selected: Boolean,
    icon: Int?,
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
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = if (selected) Color.White else Color(0xFF111827),
                modifier = Modifier.size(14.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))
        }

        Text(
            text = text,
            color = if (selected) Color.White else Color(0xFF111827),
            fontSize = 12.sp,
            fontWeight = FontWeight.W700
        )
    }
}

@Composable
private fun PlayerSearchResultCard(
    searchItem: PlayerSearchUiItem,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    val item = searchItem.item

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(14.dp),
                clip = false
            )
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (searchItem.isVideo) {
            VideoThumbnail(
                path = item.filePath,
                width = 102,
                height = 64,
                overlayIcon = {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE00004)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_play),
                            contentDescription = "play",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(width = 102.dp, height = 64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8DADD)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE00004)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "audio",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                color = Color(0xFF111827),
                fontSize = 13.sp,
                fontWeight = FontWeight.W600,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = buildSearchMetaText(
                    item = item,
                    isVideo = searchItem.isVideo
                ),
                color = Color(0xFF6B7280),
                fontSize = 10.sp,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Icon(
            imageVector = Icons.Default.MoreHoriz,
            contentDescription = "more",
            tint = Color(0xFF6B7280),
            modifier = Modifier
                .size(22.dp)
                .clickable(onClick = onMoreClick)
        )
    }
}

private fun buildSearchMetaText(
    item: PlayerUiItem,
    isVideo: Boolean,
): String {
    val type = if (isVideo) "MP4" else "MP3"
    val quality = item.quality.takeIf { it.isNotBlank() }

    return buildString {
        if (quality != null && isVideo) {
            append(quality)
            append(" • ")
        }

        append(item.size)
        append(" • ")
        append(type)
    }
}

@Composable
fun AudioCard(
    item: PlayerUiItem,
    onClick: () -> Unit,
    onMoreClick: (PlayerUiItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(
                color = Color(0xFFE5E7EB),
                width = 1.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE00004).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_audio),
                contentDescription = "audio",
                tint = Color(0xFFE00004),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                color = Color(0xFF1F2937),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${item.duration} - ${item.size}",
                fontSize = 10.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.W400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "more",
            tint = Color(0xFF6B7280),
            modifier = Modifier
                .size(22.dp)
                .clickable {
                    onMoreClick(item)
                }
        )
    }
}

@Composable
fun VideoCard(
    item: PlayerUiItem,
    onClick: () -> Unit,
    onMoreClick: (PlayerUiItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(
                color = Color(0xFFE5E7EB),
                width = 1.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VideoThumbnail(
            path = item.filePath,
            width = 75,
            height = 75,
            overlayIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = "play icon",
                    tint = Color.Transparent,
                    modifier = Modifier.size(16.dp)
                )
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                color = Color(0xFF1F2937),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${item.duration} - ${item.size} - ${item.quality}",
                fontSize = 10.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.W400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "more",
            tint = Color(0xFF6B7280),
            modifier = Modifier
                .size(22.dp)
                .clickable {
                    onMoreClick(item)
                }
        )
    }
}
@Composable
private fun PlayerNativeAdItem(
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: NativeAdPlacementConfig,
    slotKey: String,
) {
    NativeAdHost(
        nativeAd = nativeAd,
        nativeAdConfig = nativeAdConfig,
        placementConfig = placementConfig,
        placementKey = "${NativeAdConfig.PLAYER_LIST}_$slotKey"
    )
}

@Composable
fun AudioCard(
    item: PlayerUiItem,
    state: PlayerState,
    viewModel: PlayerViewModel,
    onCLick: () -> Unit,
    onMoreClick: (PlayerUiItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false // important
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(color = Color(0xFfE5E7EB), width = 1.dp, shape = RoundedCornerShape(16.dp))
            .padding(14.dp)
            .clickable {
                onCLick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE00004).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_audio),
                contentDescription = "audio",
                tint = Color(0xFFE00004),
                modifier = Modifier.size(22.dp)
            )
        }



        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "${item.duration} - ${item.size} MB",
                fontSize = 10.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.W400
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            modifier = Modifier.clickable {
                onMoreClick(
                    item
                )
            },
        )
    }
}

@Composable
fun VideoCard(
    item: PlayerUiItem,
    state: PlayerState,
    viewModel: PlayerViewModel,
    onCLick: () -> Unit,
    onMoreClick: (PlayerUiItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false // important
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(color = Color(0xFfE5E7EB), width = 1.dp, shape = RoundedCornerShape(16.dp))
            .padding(14.dp)
            .clickable {
                onCLick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Thumbnail
        VideoThumbnail(
            path = item.filePath,
            width = 75,
            height = 75,
            overlayIcon = {

                Icon(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = "play icon",
                    tint = Color.Transparent,
                    modifier = Modifier
                        .size(16.dp)
                )


            }
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "${item.duration} - ${item.size} MB - ${item.quality}",
                fontSize = 10.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.W400
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            modifier = Modifier.clickable {
                onMoreClick(item)
            }
        )
    }
}


