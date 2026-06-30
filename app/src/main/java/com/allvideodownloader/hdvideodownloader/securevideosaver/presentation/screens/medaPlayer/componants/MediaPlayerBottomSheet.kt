package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.events.VideoOptionsIntent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerBottomSheet(
    state: MediaFile,
    playbackSpeed: Float,
    onIntent: (VideoOptionsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = {
            onIntent(VideoOptionsIntent.OnDismiss)
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        containerColor = Color.White,
        dragHandle = {
            BottomSheetDragHandle()
        },
        modifier = modifier
    ) {
        if (state.isVideo) {
            VideoOptionsBottomSheetContent(
                media = state,
                playbackSpeed = playbackSpeed.toPlaybackSpeedLabel(),
                onIntent = onIntent
            )
        } else {
            AudioOptionsBottomSheetContent(
                media = state,
                fileMeta = "MP3 • 320kbps • 8.4 MB",
                playbackSpeed = playbackSpeed.toPlaybackSpeedLabel(),
                onIntent = onIntent
            )
        }
    }
}

@Composable
private fun AudioOptionsBottomSheetContent(
    media: MediaFile,
    fileMeta: String,
    playbackSpeed: String,
    onIntent: (VideoOptionsIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 30.dp)
    ) {
        AudioOptionsHeader(
            fileName = media.fileName,
            fileMeta = fileMeta
        )

        Spacer(modifier = Modifier.height(18.dp))

        AudioOptionItem(
            icon = R.drawable.ic_playback_speed,
            title = stringResource(R.string.media_option_playback_speed),
            trailingText = playbackSpeed,
            onClick = {
                onIntent(VideoOptionsIntent.OnPlaybackSpeedClicked)
            }
        )
        Spacer(modifier = Modifier.height(14.dp))

        AudioOptionItem(
            icon = R.drawable.ic_equalizer,
            title = stringResource(R.string.media_option_equalizer),
            onClick = {
                onIntent(VideoOptionsIntent.OnAudioEffectsClicked)
            }
        )
        Spacer(modifier = Modifier.height(14.dp))

        AudioOptionItem(
            icon = R.drawable.ic_music_note,
            title = stringResource(R.string.media_option_set_as_ringtone),
            onClick = {
                onIntent(VideoOptionsIntent.OnSetAsRingtoneClicked)
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        AudioOptionItem(
            icon = R.drawable.ic_share,
            title = stringResource(R.string.media_option_share_track),
            onClick = {
                onIntent(VideoOptionsIntent.OnShareClicked)
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        AudioOptionItem(
            icon = R.drawable.ic_info,
            title = stringResource(R.string.media_option_file_info),
            onClick = {
                onIntent(VideoOptionsIntent.OnFileInfoClicked)
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        AudioOptionItem(
            icon = R.drawable.ic_edit,
            title = stringResource(R.string.media_option_rename_file),
            onClick = {
                onIntent(VideoOptionsIntent.OnRenameClicked)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider(
            thickness = DividerDefaults.Thickness,
            color = Color(0xFFE9EDF3)
        )

        Spacer(modifier = Modifier.height(18.dp))

        AudioOptionItem(
            icon = R.drawable.ic_delete,
            title = stringResource(R.string.media_option_delete_file),
            iconTint = Color(0xFFE00004),
            textColor = Color(0xFFE00004),
            iconBackground = Color(0xFFE00004).copy(alpha = 0.08f),
            onClick = {
                onIntent(VideoOptionsIntent.OnDeleteClicked)
            }
        )

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun VideoOptionsBottomSheetContent(
    media: MediaFile,
    playbackSpeed: String,
    onIntent: (VideoOptionsIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .padding(bottom = 30.dp)
    ) {
        VideoOptionsHeader(
            fileName = media.fileName,
            fileMeta = "1.2 GB  •  1080p  •  02:14:30"
        )

        Spacer(modifier = Modifier.height(34.dp))

        PlaybackSpeedOptionItem(
            speed = playbackSpeed,
            onClick = {
                onIntent(VideoOptionsIntent.OnPlaybackSpeedClicked)
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        VideoOptionItem(
            icon = R.drawable.ic_equalizer,
            title = stringResource(R.string.media_option_equalizer),
            onClick = {
                onIntent(VideoOptionsIntent.OnAudioEffectsClicked)
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        VideoOptionItem(
            icon = R.drawable.ic_info,
            title = stringResource(R.string.media_option_file_info),
            onClick = {
                onIntent(VideoOptionsIntent.OnFileInfoClicked)
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        VideoOptionItem(
            icon = R.drawable.ic_share,
            title = stringResource(R.string.media_option_share_video),
            onClick = {
                onIntent(VideoOptionsIntent.OnShareClicked)
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        VideoOptionItem(
            icon = R.drawable.ic_edit,
            title = stringResource(R.string.media_option_rename_file),
            onClick = {
                onIntent(VideoOptionsIntent.OnRenameClicked)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            thickness = DividerDefaults.Thickness,
            color = Color(0xFFE9EDF3)
        )

        Spacer(modifier = Modifier.height(18.dp))

        VideoOptionItem(
            icon = R.drawable.ic_delete,
            title = stringResource(R.string.media_option_delete_file),
            iconTint = Color(0xFFE00004),
            textColor = Color(0xFFE00004),
            iconBackground = Color(0xFFE00004).copy(alpha = 0.08f),
            onClick = {
                onIntent(VideoOptionsIntent.OnDeleteClicked)
            }
        )

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun AudioOptionsHeader(
    fileName: String,
    fileMeta: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.music_player_thumbnail),
            contentDescription = null,
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = fileName,
                fontSize = 20.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF111827),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = fileMeta,
                fontSize = 15.sp,
                fontWeight = FontWeight.W500,
                color = Color(0xFF6B7280),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun VideoOptionsHeader(
    fileName: String,
    fileMeta: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = fileName,
            fontSize = 20.sp,
            fontWeight = FontWeight.W700,
            color = Color(0xFF111827),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = fileMeta,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            color = Color(0xFF6B7280),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AudioHighlightedOptionItem(
    icon: Int,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFF1F3))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AudioOptionIconContainer(
            icon = icon,
            iconTint = Color(0xFFE00004),
            backgroundColor = Color(0xFFE00004).copy(alpha = 0.10f)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.W700,
            color = Color(0xFFE00004),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PlaybackSpeedOptionItem(
    speed: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFFFF1F3))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OptionIconContainer(
            icon = R.drawable.ic_playback_speed,
            iconTint = Color(0xFFE00004),
            backgroundColor = Color(0xFFE00004).copy(alpha = 0.10f)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = stringResource(R.string.media_option_playback_speed),
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            color = Color(0xFFE00004),
            modifier = Modifier.weight(1f)
        )

        SpeedBadge(
            speed = speed
        )
    }
}

@Composable
private fun AudioOptionItem(
    icon: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    iconTint: Color = Color(0xFF111827),
    textColor: Color = Color(0xFF111827),
    iconBackground: Color = Color(0xFFF3F4F6),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AudioOptionIconContainer(
            icon = icon,
            iconTint = iconTint,
            backgroundColor = iconBackground
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.W500,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (trailingText != null) {
            Text(
                text = trailingText,
                fontSize = 14.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFFE00004)
            )
        }
    }
}

@Composable
private fun VideoOptionItem(
    icon: Int,
    title: String,
    onClick: () -> Unit,
    iconTint: Color = Color(0xFF111827),
    textColor: Color = Color(0xFF111827),
    iconBackground: Color = Color(0xFFF3F4F6),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OptionIconContainer(
            icon = icon,
            iconTint = iconTint,
            backgroundColor = iconBackground
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AudioOptionIconContainer(
    icon: Int,
    iconTint: Color,
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun OptionIconContainer(
    icon: Int,
    iconTint: Color,
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SpeedBadge(
    speed: String,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(Color(0xFFE00004))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = speed,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.W700
        )
    }
}

@Composable
 fun BottomSheetDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 14.dp, bottom = 12.dp)
            .size(width = 54.dp, height = 5.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(Color(0xFFE5E7EB))
    )
}

@Stable
private fun Float.toPlaybackSpeedLabel(): String {
    return when {
        this == 1f -> "1x"
        this % 1f == 0f -> "${this.toInt()}x"
        else -> "${this}x"
    }
}