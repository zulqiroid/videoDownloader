package com.app.videodownloader.presentation.screens.medaPlayer.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.presentation.screens.downloadGuide.events.DownloadGuideIntent
import com.app.videodownloader.presentation.screens.downloadGuide.state.DownloadGuideState
import com.app.videodownloader.presentation.screens.downloadGuide.state.GuideStep
import com.app.videodownloader.presentation.screens.medaPlayer.events.VideoOptionsIntent
import com.app.videodownloader.presentation.screens.medaPlayer.states.VideoOptionsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerBottomSheet(
    state: MediaFile,
    onIntent: (VideoOptionsIntent) -> Unit
){


    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = {
            onIntent(VideoOptionsIntent.OnDismiss)
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color(0xFFF9FAFB),
        dragHandle = {
            Spacer(Modifier.width(48.dp).height(8.dp).padding(vertical = 10.dp).background(Color(0xFFF3F4F6)))
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // Header
            Text(
                text = state.fileName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = state.fileName,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(20.dp))

            // Playback speed (highlighted item)
            HighlightedActionItem(
                icon = Icons.Default.Speed,
                text = "Playback speed",
                trailing = {
                   // SpeedBadge(state.playbackSpeed)
                },
                onClick = { onIntent(VideoOptionsIntent.OnPlaybackSpeedClicked) }
            )

            Spacer(Modifier.height(8.dp))

            // Regular actions
            ActionItem(
                icon = R.drawable.ic_info,
                text = "File info"
            ) {
                onIntent(VideoOptionsIntent.OnFileInfoClicked)
            }

            ActionItem(
                icon = R.drawable.ic_share,
                text = if (state.isVideo) "Share video" else "Share track"
            ) {
                onIntent(VideoOptionsIntent.OnShareClicked)
            }

            ActionItem(
                icon = R.drawable.ic_edit,
                text = "Rename file"
            ) {
                onIntent(VideoOptionsIntent.OnRenameClicked)
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, Color(0xFFE5E7EB).copy(alpha = 0.6f))
            Spacer(Modifier.height(12.dp))

            // Delete (danger)
            ActionItem(
                icon = R.drawable.ic_delete,
                text = "Delete file",
                color = Color.Red,
            ) {
                onIntent(VideoOptionsIntent.OnDeleteClicked)
            }

            Spacer(Modifier.height(24.dp))
        }

    }
}

@Composable
fun HighlightedActionItem(
    icon: ImageVector,
    text: String,
    trailing: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFDEAEA))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Red)

        Spacer(Modifier.width(12.dp))

        Text(
            text = text,
            color = Color.Red,
            modifier = Modifier.weight(1f)
        )

        trailing()
    }
}

@Composable
fun SpeedBadge(speed: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.Red)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = speed,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun ActionItem(
    icon: Int,
    text: String,
    color: Color = Color.Black,
    background: Color = Color.Transparent,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
            )

        Spacer(Modifier.width(16.dp))

        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.W500,
            fontSize = 16.sp
            )
    }
}