package com.app.videodownloader.presentation.screens.download.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.DownloadStatus
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.presentation.screens.download.componants.CompletedCard
import com.app.videodownloader.presentation.screens.download.componants.DownloadTabs
import com.app.videodownloader.presentation.screens.download.componants.DownloadingCard
import com.app.videodownloader.presentation.screens.download.states.DownloadTab
import com.app.videodownloader.presentation.screens.download.states.DownloadUiItem
import com.app.videodownloader.presentation.screens.download.states.toMediaFile
import com.app.videodownloader.presentation.screens.download.viewModel.DownloadViewModel
import com.app.videodownloader.presentation.screens.player.states.toMediaFile
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel = koinViewModel(),
    sendToMedia: (
        mediaList: List<MediaFile>,
        startIndex: Int,
    ) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val list = when (state.selectedTab) {
        DownloadTab.DOWNLOADING -> state.downloading
        DownloadTab.COMPLETED -> state.completed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Tabs
        DownloadTabs(
            selected = state.selectedTab,
            downloadingCount = state.downloading.size,
            completedCount = state.completed.size,
            onTabSelected = {
                viewModel.updateTab(it)
            }
        )

        LazyColumn {
            itemsIndexed(list, key = { _, item -> item.id }) { indexed, item ->
                if (state.selectedTab == DownloadTab.DOWNLOADING) {
                    DownloadingCard(item= item, state = state , viewModel = viewModel)
                } else {
                    CompletedCard(
                        item = item,
                        state = state ,
                        viewModel = viewModel,
                        onItemCLicked = {
                            sendToMedia(
                                state.completed.map { it.toMediaFile(true) },
                                indexed
                            )
                        }
                        )
                }
            }
        }
    }
}




