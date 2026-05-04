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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import com.app.videodownloader.presentation.screens.download.componants.VideoThumbnail
import com.app.videodownloader.presentation.screens.download.states.DownloadTab
import com.app.videodownloader.presentation.screens.download.states.DownloadUiItem
import com.app.videodownloader.presentation.screens.download.viewModel.DownloadViewModel
import com.app.videodownloader.presentation.screens.player.componants.MediaTabs
import com.app.videodownloader.presentation.screens.player.componants.PlayAllButton
import com.app.videodownloader.presentation.screens.main.events.FileDialogIntent
import com.app.videodownloader.presentation.screens.player.states.PlayerState
import com.app.videodownloader.presentation.screens.player.states.PlayerTab
import com.app.videodownloader.presentation.screens.player.states.PlayerUiItem
import com.app.videodownloader.presentation.screens.player.states.toMediaFile
import com.app.videodownloader.presentation.screens.player.viewModel.PlayerViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.io.File


@Composable
fun FileOptionsDialog(
    item: MediaFile?,
    onIntent: (FileDialogIntent) -> Unit,
) {
    if (item == null) return

    Dialog(
        onDismissRequest = { onIntent(FileDialogIntent.OnDismiss) }
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item?.fileName ?: "media",
                            fontWeight = FontWeight.W700,
                            color = Color(0xFF111827),
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Stored in: ${item?.filePath}",
                            fontWeight = FontWeight.W500,
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE00004).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(if (item.isVideo)R.drawable.ic_pictorial_reel_filled else R.drawable.ic_audio),
                            contentDescription = null,
                            tint = Color(0xFFE00004),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                }

                Spacer(Modifier.height(20.dp))

                // Play Button
                Button(
                    onClick = { onIntent(FileDialogIntent.OnPlayClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE50914) // Netflix red style
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Play Now",
                        fontWeight = FontWeight.W700,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Quick Actions
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    QuickActionButton(
                        text = "Share File",
                        icon = R.drawable.ic_share,
                        onClick = { onIntent(FileDialogIntent.OnShareClicked) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(
                    Modifier.height(1.dp),
                    DividerDefaults.Thickness,
                    Color(0xFFE5E7EB).copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(8.dp))

                // List Actions
                ActionItem(
                    text = "Rename",
                    icon = R.drawable.ic_edit,

                    ) {
                    onIntent(FileDialogIntent.OnRenameClicked)
                }

                ActionItem(
                    text = "Move to folder",
                    icon = R.drawable.ic_move_folder,

                    ) {
                    onIntent(FileDialogIntent.OnMoveClicked)
                }

                ActionItem(
                    text = "File info",
                    icon = R.drawable.ic_info,
                    color = Color(0xFF111827)
                ) {
                    onIntent(FileDialogIntent.OnInfoClicked)
                }

                ActionItem(
                    text = "Delete file",
                    icon = R.drawable.ic_delete,
                    color = Color(0xFFE00004)
                ) {
                    onIntent(FileDialogIntent.OnDeleteClicked)
                }

                Spacer(Modifier.height(12.dp))

                // Dismiss
                TextButton(
                    onClick = { onIntent(FileDialogIntent.OnDismiss) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Dismiss",
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp,
                        color = Color(0xFF6B7280))
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Button(
        onClick = { onClick()  },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF2F2F2) // Netflix red style
        )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color(0xFF111827),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = text,
            fontWeight = FontWeight.W700,
            color = Color(0xFF111827),
            fontSize = 16.sp
        )
    }
}


@Composable
fun ActionItem(
    text: String,
    icon: Int,
    color: Color = Color.Black,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = color
        )
    }
}