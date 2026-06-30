package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.MediaPlaybackState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants.formatTime

@Composable
fun AudioMiniPlayer(
    state: MediaPlaybackState,
    onPlayerClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentMedia = state.currentMedia ?: return

    val progress = remember(
        state.positionMs,
        state.durationMs
    ) {
        if (state.durationMs <= 0L) {
            0f
        } else {
            (state.positionMs.toFloat() / state.durationMs.toFloat())
                .coerceIn(0f, 1f)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .dropShadow(
                shape = RoundedCornerShape(22.dp),
                shadow = Shadow(
                    radius = 12.dp,
                    spread = 1.dp,
                    color = Color.Black.copy(alpha = 0.16f),
                    offset = DpOffset(0.dp, 2.dp)
                )
            )
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .clickable(onClick = onPlayerClick)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = Color(0xFFE00004),
            trackColor = Color(0xFFF2F4F7),
            strokeCap = StrokeCap.Round
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 4.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFECEC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_music_note),
                    contentDescription = null,
                    tint = Color(0xFFE00004),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = currentMedia.fileName,
                    color = Color(0xFF101828),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W700,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${formatTime(state.positionMs)} / ${formatTime(state.durationMs)}",
                    color = Color(0xFF6B7280),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W500,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = onPreviousClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_previous),
                    contentDescription = null,
                    tint = Color(0xFF101828),
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE00004))
            ) {
                Icon(
                    painter = painterResource(
                        if (state.isPlaying) {
                            R.drawable.ic_pause
                        } else {
                            R.drawable.ic_play
                        }
                    ),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onNextClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_next),
                    contentDescription = null,
                    tint = Color(0xFF101828),
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}