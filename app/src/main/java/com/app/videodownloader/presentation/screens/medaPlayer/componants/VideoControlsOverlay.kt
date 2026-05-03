package com.app.videodownloader.presentation.screens.medaPlayer.componants

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.app.videodownloader.presentation.screens.medaPlayer.states.MediaPlayerState
import com.app.videodownloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VideoControlsOverlay(
    media: MediaFile,
    state: MediaPlayerState,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onForward: () -> Unit,
    onRewind: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onMuteToggle: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onThreeDotsClick:() -> Unit,

    ) {
    var showVolumeSlider by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.25f))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack,
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF191919)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Text(
                text = media.fileName,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                maxLines = 1
            )

            IconButton(
                onClick = {
                    onThreeDotsClick()
                },
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF191919)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CircleButton(
                icon = R.drawable.ic_10_sec_forward,
                onClick = onRewind
            )

            CircleButton(
                icon = R.drawable.ic_10_sec_backward,
                onClick = onForward
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {

            VideoSeekBar(
                position = state.position,
                duration = state.duration,
                onSeek = onSeek
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatTime(state.position),
                        fontWeight = FontWeight.W500,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = " / ",
                        fontWeight = FontWeight.W500,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = formatTime(state.duration),
                        fontWeight = FontWeight.W500,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.ic_src_rotate),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.ic_src_resize),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (showVolumeSlider) {
                Slider(
                    value = if (state.isMuted) 0f else state.volume,
                    onValueChange = onVolumeChange,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color(0xFFE00004),
                        inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(R.drawable.ic_speaker),
                    contentDescription = null,
                    tint = if (state.isMuted) Color.Red else Color.White,
                    modifier = Modifier
                        .size(34.dp)
                        .clickable {
                            showVolumeSlider = !showVolumeSlider
                            if (!showVolumeSlider) onMuteToggle()
                        }
                )

                Icon(
                    painter = painterResource(R.drawable.ic_previous),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onPrevious() }
                )

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE00004))
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.ic_next),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onNext() }
                )

                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}
