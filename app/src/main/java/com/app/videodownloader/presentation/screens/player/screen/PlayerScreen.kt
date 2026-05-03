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

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = koinViewModel(),
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
    onMoreClick: (MediaFile) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val list = when (state.selectedTab) {
        PlayerTab.VIDEO -> state.videos
        PlayerTab.AUDIO -> state.audios
    }

    val nativePlacementKey = NativeAdConfig.PLAYER_LIST
    val nativePlacementConfig = state.nativeAdConfig.placement(nativePlacementKey)
    val nativeAd = state.nativeAds[nativePlacementKey]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        MediaTabs(
            selectedTab = state.selectedTab,
            onTabChange = viewModel::onTabChange
        )

        Spacer(Modifier.height(16.dp))

        PlayAllButton(
            state = state,
            viewModel = viewModel,
            onClick = {
                if (state.selectedTab == PlayerTab.VIDEO) {
                    sendToMedia(
                        state.videos.map { it.toMediaFile(true) },
                        0
                    )
                } else {
                    sendToMedia(
                        state.audios.map { it.toMediaFile(false) },
                        0
                    )
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (
                nativePlacementConfig != null &&
                NativeAdListHelper.shouldShowAdAfterItem(
                    index = -1,
                    totalItems = list.size,
                    config = nativePlacementConfig
                )
            ) {
                item(key = "player_native_ad_start_${state.selectedTab}") {
                    PlayerNativeAdItem(
                        nativeAd = nativeAd,
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = nativePlacementConfig
                    )
                }
            }

            itemsIndexed(
                items = list,
                key = { _, item -> item.id }
            ) { index, item ->

                if (state.selectedTab == PlayerTab.VIDEO) {
                    VideoCard(
                        item = item,
                        state = state,
                        viewModel = viewModel,
                        onCLick = {
                            sendToMedia(
                                state.videos.map { it.toMediaFile(true) },
                                index
                            )
                        },
                        onMoreClick = {
                            onMoreClick(
                                it.toMediaFile(true)
                            )
                        }
                    )
                } else {
                    AudioCard(
                        item = item,
                        state = state,
                        viewModel = viewModel,
                        onCLick = {
                            sendToMedia(
                                state.audios.map { it.toMediaFile(false) },
                                index
                            )
                        },
                        onMoreClick = {
                            onMoreClick(
                                it.toMediaFile(false)
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
                    PlayerNativeAdItem(
                        nativeAd = nativeAd,
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = nativePlacementConfig
                    )
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun PlayerNativeAdItem(
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: NativeAdPlacementConfig
) {
    NativeAdHost(
        nativeAd = nativeAd,
        nativeAdConfig = nativeAdConfig,
        placementConfig = placementConfig,
        placementKey = NativeAdConfig.PLAYER_LIST
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
            modifier = Modifier.clickable{
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


