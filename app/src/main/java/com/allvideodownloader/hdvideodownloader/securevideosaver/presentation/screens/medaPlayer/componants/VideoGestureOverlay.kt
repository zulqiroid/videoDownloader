package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Brightness6
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.states.MediaPlayerState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.states.VideoGestureControlType
import kotlin.math.roundToInt

@Composable
fun VideoGestureOverlay(
    state: MediaPlayerState,
    gesturesEnabled: Boolean,
    onGestureChanged: (
        type: VideoGestureControlType,
        progress: Float,
        label: String
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val activity = remember(context) {
        context.findActivity()
    }

    val audioManager = remember(context) {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        var gestureType by remember {
            mutableStateOf<VideoGestureControlType?>(null)
        }

        var dragStartBrightness by remember {
            mutableFloatStateOf(DEFAULT_BRIGHTNESS)
        }

        var dragStartVolume by remember {
            mutableIntStateOf(0)
        }

        var accumulatedDragPx by remember {
            mutableFloatStateOf(0f)
        }

        var lastAppliedVolume by remember {
            mutableIntStateOf(-1)
        }

        val maxVolume = remember(audioManager) {
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                .coerceAtLeast(1)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(
                    gesturesEnabled,
                    maxVolume,
                    activity
                ) {
                    if (!gesturesEnabled || activity == null) return@pointerInput

                    detectVerticalDragGestures(
                        onDragStart = { startOffset ->
                            val isLeftSide = startOffset.x < size.width / 2f

                            gestureType = if (isLeftSide) {
                                VideoGestureControlType.Brightness
                            } else {
                                VideoGestureControlType.Volume
                            }

                            accumulatedDragPx = 0f
                            dragStartBrightness = activity.readCurrentWindowBrightness()
                            dragStartVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                            lastAppliedVolume = dragStartVolume
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()

                            accumulatedDragPx += dragAmount

                            val type = gestureType ?: return@detectVerticalDragGestures
                            val heightPx = size.height.toFloat().coerceAtLeast(1f)

                            when (type) {
                                VideoGestureControlType.Brightness -> {
                                    val progress = calculateBrightnessFromDrag(
                                        startBrightness = dragStartBrightness,
                                        accumulatedDragPx = accumulatedDragPx,
                                        heightPx = heightPx
                                    )

                                    activity.setWindowBrightness(progress)

                                    onGestureChanged(
                                        type,
                                        progress,
                                        progress.toPercentLabel()
                                    )
                                }

                                VideoGestureControlType.Volume -> {
                                    val newVolume = calculateVolumeFromDrag(
                                        startVolume = dragStartVolume,
                                        accumulatedDragPx = accumulatedDragPx,
                                        heightPx = heightPx,
                                        maxVolume = maxVolume
                                    )

                                    if (newVolume != lastAppliedVolume) {
                                        audioManager.setStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            newVolume,
                                            0
                                        )

                                        lastAppliedVolume = newVolume
                                    }

                                    val progress = newVolume.toFloat() / maxVolume.toFloat()

                                    onGestureChanged(
                                        type,
                                        progress,
                                        progress.toPercentLabel()
                                    )
                                }
                            }
                        },
                        onDragEnd = {
                            gestureType = null
                            accumulatedDragPx = 0f
                        },
                        onDragCancel = {
                            gestureType = null
                            accumulatedDragPx = 0f
                        }
                    )
                }
        )

        AnimatedVisibility(
            visible = state.showGestureFeedback &&
                    state.gestureControlType != null,
            enter = fadeIn() + scaleIn(initialScale = 0.92f),
            exit = fadeOut() + scaleOut(targetScale = 0.92f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            GestureFeedbackCard(
                type = state.gestureControlType ?: VideoGestureControlType.Volume,
                progress = state.gestureProgress,
                label = state.gestureLabel
            )
        }
    }
}

private fun calculateBrightnessFromDrag(
    startBrightness: Float,
    accumulatedDragPx: Float,
    heightPx: Float,
): Float {
    val delta = -(accumulatedDragPx / heightPx) * BRIGHTNESS_SENSITIVITY

    return (startBrightness + delta)
        .coerceIn(
            minimumValue = MIN_BRIGHTNESS,
            maximumValue = MAX_BRIGHTNESS
        )
}

private fun calculateVolumeFromDrag(
    startVolume: Int,
    accumulatedDragPx: Float,
    heightPx: Float,
    maxVolume: Int,
): Int {
    val delta = -(accumulatedDragPx / heightPx) *
            maxVolume.toFloat() *
            VOLUME_SENSITIVITY

    return (startVolume + delta.roundToInt())
        .coerceIn(
            minimumValue = 0,
            maximumValue = maxVolume
        )
}

@Composable
private fun GestureFeedbackCard(
    type: VideoGestureControlType,
    progress: Float,
    label: String,
) {
    val safeProgress = progress.coerceIn(0f, 1f)

    val title = when (type) {
        VideoGestureControlType.Brightness -> "Brightness"
        VideoGestureControlType.Volume -> "Volume"
    }

    val icon = when {
        type == VideoGestureControlType.Brightness -> {
            Icons.Outlined.Brightness6
        }

        safeProgress <= 0f -> {
            Icons.Filled.VolumeOff
        }

        else -> {
            Icons.Filled.VolumeUp
        }
    }

    Column(
        modifier = Modifier
            .width(164.dp)
            .background(
                color = Color.Black.copy(alpha = 0.76f),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color.White.copy(alpha = 0.13f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.W900
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.82f),
            fontSize = 12.sp,
            fontWeight = FontWeight.W800
        )

        Spacer(modifier = Modifier.height(13.dp))

        GestureSegmentProgress(
            progress = safeProgress
        )
    }
}

@Composable
private fun GestureSegmentProgress(
    progress: Float,
) {
    val activeSegments = (progress.coerceIn(0f, 1f) * PROGRESS_SEGMENT_COUNT)
        .roundToInt()
        .coerceIn(0, PROGRESS_SEGMENT_COUNT)

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(PROGRESS_SEGMENT_COUNT) { index ->
            val isActive = index < activeSegments
            val segmentHeight = when {
                isActive -> 22.dp
                else -> 13.dp
            }

            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(segmentHeight)
                    .background(
                        color = if (isActive) {
                            Color(0xFFE00004)
                        } else {
                            Color.White.copy(alpha = 0.22f)
                        },
                        shape = RoundedCornerShape(100.dp)
                    )
            )
        }
    }
}

private fun Activity.readCurrentWindowBrightness(): Float {
    val currentValue = window.attributes.screenBrightness

    return when {
        currentValue in MIN_BRIGHTNESS..MAX_BRIGHTNESS -> currentValue
        else -> DEFAULT_BRIGHTNESS
    }
}

private fun Activity.setWindowBrightness(
    brightness: Float
) {
    val safeBrightness = brightness.coerceIn(
        minimumValue = MIN_BRIGHTNESS,
        maximumValue = MAX_BRIGHTNESS
    )

    val layoutParams = window.attributes
    layoutParams.screenBrightness = safeBrightness
    window.attributes = layoutParams
}

private fun Float.toPercentLabel(): String {
    return "${(coerceIn(0f, 1f) * 100f).roundToInt()}%"
}

@Stable
private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

private const val DEFAULT_BRIGHTNESS = 0.5f
private const val MIN_BRIGHTNESS = 0.02f
private const val MAX_BRIGHTNESS = 1f

private const val BRIGHTNESS_SENSITIVITY = 1.15f
private const val VOLUME_SENSITIVITY = 1.35f
private const val PROGRESS_SEGMENT_COUNT = 12