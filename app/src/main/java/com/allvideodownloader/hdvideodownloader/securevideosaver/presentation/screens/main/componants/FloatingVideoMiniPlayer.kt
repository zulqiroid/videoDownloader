package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.componants

import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.FloatingVideoMiniPlayerState
import kotlin.math.roundToInt

@OptIn(UnstableApi::class)
@Composable
fun FloatingVideoMiniPlayer(
    state: FloatingVideoMiniPlayerState,
    player: Player?,
    onPlayerClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!state.hasActiveVideo || player == null) return

    val density = LocalDensity.current
    val context = LocalContext.current

    val playerView = remember {
        PlayerView(context).apply {
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            keepScreenOn = false
        }
    }

    DisposableEffect(playerView) {
        onDispose {
            playerView.player = null
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val cardWidth = 252.dp
        val cardHeight = 158.dp

        val maxDragLeftPx = with(density) {
            (maxWidth - cardWidth - 12.dp).toPx().coerceAtLeast(0f)
        }

        val maxDragUpPx = with(density) {
            (maxHeight - cardHeight - 110.dp).toPx().coerceAtLeast(0f)
        }

        var targetOffsetX by remember { mutableFloatStateOf(0f) }
        var targetOffsetY by remember { mutableFloatStateOf(0f) }

        val animatedOffsetX by animateFloatAsState(
            targetValue = targetOffsetX,
            animationSpec = tween(durationMillis = 180),
            label = "floating_video_mini_player_x"
        )

        val animatedOffsetY by animateFloatAsState(
            targetValue = targetOffsetY,
            animationSpec = tween(durationMillis = 180),
            label = "floating_video_mini_player_y"
        )

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

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .offset {
                    IntOffset(
                        x = animatedOffsetX.roundToInt(),
                        y = animatedOffsetY.roundToInt()
                    )
                }
                .width(cardWidth)
                .height(cardHeight)
                .shadow(
                    elevation = 22.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color.Black.copy(alpha = 0.22f),
                    spotColor = Color.Black.copy(alpha = 0.34f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
                .pointerInput(maxDragLeftPx, maxDragUpPx) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()

                            targetOffsetX = (targetOffsetX + dragAmount.x)
                                .coerceIn(
                                    minimumValue = -maxDragLeftPx,
                                    maximumValue = 0f
                                )

                            targetOffsetY = (targetOffsetY + dragAmount.y)
                                .coerceIn(
                                    minimumValue = -maxDragUpPx,
                                    maximumValue = 0f
                                )
                        },
                        onDragEnd = {
                            targetOffsetX = if (targetOffsetX < -maxDragLeftPx / 2f) {
                                -maxDragLeftPx
                            } else {
                                0f
                            }
                        },
                        onDragCancel = {
                            targetOffsetX = if (targetOffsetX < -maxDragLeftPx / 2f) {
                                -maxDragLeftPx
                            } else {
                                0f
                            }
                        }
                    )
                }
        ) {
            AndroidView(
                factory = {
                    playerView
                },
                update = { view ->
                    if (view.player !== player) {
                        view.player = player
                    }

                    view.useController = false
                    view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onPlayerClick
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.12f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.86f)
                            )
                        )
                    )
            )

            FloatingMiniPlayerTopBar(
                isPlaying = state.isPlaying,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 9.dp)
            )

            FloatingMiniPlayerCloseButton(
                isPlaying = state.isPlaying,
                onClick = onCloseClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 8.dp)
            )

            FloatingMiniPlayerBottomContent(
                fileName = state.currentMedia?.fileName.orEmpty(),
                positionMs = state.positionMs,
                durationMs = state.durationMs,
                isPlaying = state.isPlaying,
                onPlayPauseClick = onPlayPauseClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height(3.dp),
                color = FloatingVideoMiniPlayerColors.Primary,
                trackColor = Color.White.copy(alpha = 0.18f)
            )
        }
    }
}

@Composable
private fun FloatingMiniPlayerTopBar(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
) {
    Row(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.46f),
                shape = RoundedCornerShape(100.dp)
            )
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (isPlaying){
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = FloatingVideoMiniPlayerColors.Primary,
                        shape = CircleShape
                    )
            )
        }

        Text(
            text = if (isPlaying) "Playing" else "Pause",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.W900
        )
    }
}

@Composable
private fun FloatingMiniPlayerBottomContent(
    fileName: String,
    positionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
      /*  Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(17.dp)
            )
        }*/

        Spacer(modifier = Modifier.width(8.dp))

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = fileName,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.W900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${positionMs.toMiniPlayerTime()} / ${durationMs.toMiniPlayerTime()}",
                color = Color.White.copy(alpha = 0.76f),
                fontSize = 10.sp,
                fontWeight = FontWeight.W700,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        FloatingMiniPlayerPlayPauseButton(
            isPlaying = isPlaying,
            onClick = onPlayPauseClick
        )
    }
}

@Composable
private fun FloatingMiniPlayerPlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(FloatingVideoMiniPlayerColors.Primary)
    ) {
        Icon(
            painter = painterResource(
                if (isPlaying) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                }
            ),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun FloatingMiniPlayerCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(17.dp)
        )
    }
}

private fun Long.toMiniPlayerTime(): String {
    val safeValue = coerceAtLeast(0L)
    val totalSeconds = safeValue / 1_000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

private object FloatingVideoMiniPlayerColors {
    val Primary = Color(0xFFE00004)
}