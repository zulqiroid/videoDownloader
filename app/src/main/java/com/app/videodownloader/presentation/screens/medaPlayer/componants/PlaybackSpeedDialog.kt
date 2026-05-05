package com.app.videodownloader.presentation.screens.medaPlayer.componants

import androidx.annotation.StringRes
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.R
import com.app.videodownloader.presentation.componants.CloseButton
import kotlin.math.abs

@Composable
fun PlaybackSpeedDialog(
    selectedSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onResetClicked: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = Color.White,
        shape = RoundedCornerShape(18.dp),
        title = {
            PlaybackSpeedDialogHeader(
                onDismiss = onDismiss
            )
        },
        text = {
            PlaybackSpeedOptionsContent(
                selectedSpeed = selectedSpeed,
                onSpeedSelected = onSpeedSelected
            )
        },
        confirmButton = {
            TextButton(
                onClick = onResetClicked
            ) {
                Text(
                    text = stringResource(R.string.reset_to_default),
                    color = Color(0xFFE00004),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F4F6))
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = Color(0xFF374151),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500
                )
            }
        }
    )
}

@Composable
private fun PlaybackSpeedDialogHeader(
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.playback_speed),
                color = Color(0xFF111827),
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.weight(1f)
            )

            CloseButton(
                onClick = { onDismiss() },
                modifier = Modifier
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        HorizontalDivider(
            color = Color(0xFFE9EDF3)
        )
    }
}

@Composable
private fun PlaybackSpeedOptionsContent(
    selectedSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        PlaybackSpeedOption.entries.forEachIndexed { index, option ->
            PlaybackSpeedOptionRow(
                option = option,
                selected = selectedSpeed.isSamePlaybackSpeed(option.value),
                onClick = {
                    onSpeedSelected(option.value)
                }
            )

            if (index != PlaybackSpeedOption.entries.lastIndex) {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun PlaybackSpeedOptionRow(
    option: PlaybackSpeedOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (selected) {
        Color(0xFFE00004)
    } else {
        Color(0xFF111827)
    }

    val backgroundColor = if (selected) {
        Color(0xFFFEE2E2).copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(option.labelRes),
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.W500 else FontWeight.W400
        )

        PlaybackSpeedRadioButton(
            selected = selected
        )
    }
}

@Composable
private fun PlaybackSpeedRadioButton(
    selected: Boolean,
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(
                if (selected) Color(0xFFE00004) else Color(0xFFE5E7EB)
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (selected) 16.dp else 22.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE00004))
                )
            }
        }
    }
}

@Immutable
private enum class PlaybackSpeedOption(
    val value: Float,
    @StringRes val labelRes: Int,
) {
    Speed025(
        value = 0.25f,
        labelRes = R.string.playback_speed_025
    ),
    Speed05(
        value = 0.5f,
        labelRes = R.string.playback_speed_05
    ),
    Speed1(
        value = 1f,
        labelRes = R.string.playback_speed_10_normal
    ),
    Speed125(
        value = 1.25f,
        labelRes = R.string.playback_speed_125
    ),
    Speed15(
        value = 1.5f,
        labelRes = R.string.playback_speed_15
    ),
    Speed2(
        value = 2f,
        labelRes = R.string.playback_speed_20
    ),
    Speed4(
        value = 4f,
        labelRes = R.string.playback_speed_40
    )
}

@Stable
private fun Float.isSamePlaybackSpeed(other: Float): Boolean {
    return abs(this - other) < PLAYBACK_SPEED_EPSILON
}

private const val PLAYBACK_SPEED_EPSILON = 0.001f