package com.app.videodownloader.presentation.screens.player.componants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.app.videodownloader.presentation.screens.download.componants.CompletedCard
import com.app.videodownloader.presentation.screens.download.componants.DownloadTabs
import com.app.videodownloader.presentation.screens.download.componants.DownloadingCard
import com.app.videodownloader.presentation.screens.download.states.DownloadTab
import com.app.videodownloader.presentation.screens.download.states.DownloadUiItem
import com.app.videodownloader.presentation.screens.download.viewModel.DownloadViewModel
import com.app.videodownloader.presentation.screens.player.states.PlayerTab
import com.app.videodownloader.presentation.screens.player.viewModel.PlayerViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.io.File


@Composable
fun MediaTabs(
    selectedTab: PlayerTab,
    onTabChange: (PlayerTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F3F5), RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {

        TabItem(
            title = "Videos",
            headingIcon = if (selectedTab == PlayerTab.VIDEO)
                R.drawable.ic_vd_bundle_filled
            else R.drawable.ic_vd_bundle_outlined,
            selected = selectedTab == PlayerTab.VIDEO
        ) {
            onTabChange(PlayerTab.VIDEO)
        }

        TabItem(
            title = "Music",
            headingIcon = if (selectedTab == PlayerTab.AUDIO)
                R.drawable.ic_audio
            else R.drawable.ic_music_outlined,
            selected = selectedTab == PlayerTab.AUDIO
        ) {
            onTabChange(PlayerTab.AUDIO)
        }
    }
}
@Composable
private fun RowScope.TabItem(
    title: String,
    headingIcon: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            Icon(
                painter = painterResource(headingIcon),
                contentDescription = title,
                tint = if (selected) Color(0xFFE00004) else Color(0xFF868E96),
                modifier = Modifier.size(22.dp)
            )

            Text(
                text = title,
                color = if (selected) Color(0xFFE00004) else Color(0xFF868E96),
                fontWeight = FontWeight.W700,
                fontSize = 14.sp
            )
        }

    }
}