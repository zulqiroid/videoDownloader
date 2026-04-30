package com.app.videodownloader.presentation.screens.download.componants


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
import com.app.videodownloader.presentation.screens.download.componants.DownloadTabs
import com.app.videodownloader.presentation.screens.download.events.DownloadEvents
import com.app.videodownloader.presentation.screens.download.states.DownloadState
import com.app.videodownloader.presentation.screens.download.states.DownloadTab
import com.app.videodownloader.presentation.screens.download.states.DownloadUiItem
import com.app.videodownloader.presentation.screens.download.viewModel.DownloadViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@Composable
fun DownloadingCard(
    item: DownloadUiItem,
    state: DownloadState,
    viewModel: DownloadViewModel
) {

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {

        // 🔹 Top Row
        Row(verticalAlignment = Alignment.CenterVertically) {

            // 🎥 Icon Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFE5E5), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_downloading_vd_cam),
                    contentDescription = null,
                    tint = Color(0xFFE00004),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    maxLines = 1,
                    color = Color(0xFF1F2937)

                )

                Text(
                    text = "Downloading • ${item.sizeText}",
                    fontSize = 10.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // 🔴 Progress Row (bar + percentage)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            LinearProgressIndicator(
                progress = { item.progress / 100f },
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFFE00004),
                trackColor = Color(0xFFF3F4F6),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "${item.progress}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFFE00004)
            )
        }

        Spacer(Modifier.height(10.dp))

        // 🔹 Bottom Row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Quality Tag
            Box(
                modifier = Modifier
                    .background(Color(0xFFE4E4E7), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "1080p",
                    fontSize = 11.sp,
                    color = Color(0xFF52525B)
                )
            }

            Spacer(Modifier.width(10.dp))

            Text(
                text = item.timeText, // e.g. "Est. 12s left"
                fontSize = 12.sp,
                color = Color(0xFF71717A)
            )

            Spacer(modifier = Modifier.weight(1f))

            // ⏸ Pause Button
            CircleIconButton(
                icon = R.drawable.ic_pause,
                backgroundColor = Color.Transparent,
                tint = Color(0xFF52525B),
                borderColor = Color(0xFFE5E7EB),
                onClick = {viewModel.onEvent(DownloadEvents.OnPauseDownloadingClicked(item.id))}
            )

            Spacer(Modifier.width(10.dp))

            // 🗑 Delete Button
            CircleIconButton(
                icon = R.drawable.ic_delete,
                backgroundColor = Color(0xFFE00004).copy(alpha = 0.1f),
                tint = Color(0xFFE00004),
                borderColor =  Color(0xFFE00004).copy(alpha = 0.25f),
                onClick = {
                    viewModel.onEvent(DownloadEvents.OnDeleteDownloadingClicked(item.id))
                }
            )
        }
    }
}
