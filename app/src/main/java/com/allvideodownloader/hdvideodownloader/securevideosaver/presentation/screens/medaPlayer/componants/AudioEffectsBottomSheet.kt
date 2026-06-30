package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.AudioEffectsState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.AudioEqualizerBand
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.events.VideoOptionsIntent
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioEffectsBottomSheet(
    state: AudioEffectsState,
    onIntent: (VideoOptionsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val verticalScrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = {
            onIntent(VideoOptionsIntent.OnAudioEffectsDismissed)
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        containerColor = AudioEffectsColors.Surface,
        dragHandle = {
            AudioEffectsDragHandle()
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 720.dp)
                .verticalScroll(verticalScrollState)
                .padding(horizontal = 18.dp)
                .padding(bottom = 28.dp)
        ) {
            AudioEffectsHeroHeader(
                isAvailable = state.isAvailable,
                isEqualizerEnabled = state.isEqualizerEnabled,
                isBassBoostEnabled = state.isBassBoostEnabled
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (!state.isAvailable) {
                UnavailableEffectsMessage(
                    message = state.errorMessage
                        ?: stringResource(R.string.audio_effects_not_available)
                )
                return@Column
            }

            AudioEffectSwitchCard(
                title = stringResource(R.string.audio_effects_equalizer),
                subtitle = if (state.isEqualizerSupported) {
                    "${state.bands.size} bands • ${state.presets.size} presets"
                } else {
                    "Not supported on this device"
                },
                checked = state.isEqualizerEnabled,
                enabled = state.isEqualizerSupported,
                onCheckedChange = {
                    onIntent(VideoOptionsIntent.OnEqualizerEnabledChanged(it))
                }
            )

            if (state.presets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))

                SectionTitle(
                    title = stringResource(R.string.audio_effects_presets),
                    subtitle = "Quickly apply a tuned sound profile"
                )

                Spacer(modifier = Modifier.height(10.dp))

                PresetChipsRow(
                    state = state,
                    onPresetSelected = { presetIndex ->
                        onIntent(VideoOptionsIntent.OnEqualizerPresetSelected(presetIndex))
                    }
                )
            }

            if (state.bands.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(
                    title = stringResource(R.string.audio_effects_bands),
                    subtitle = "Fine tune frequencies with studio-style controls"
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfessionalEqualizerPanel(
                    bands = state.bands,
                    enabled = state.isEqualizerSupported && state.isEqualizerEnabled,
                    onBandLevelChanged = { bandIndex, levelMb ->
                        onIntent(
                            VideoOptionsIntent.OnEqualizerBandLevelChanged(
                                bandIndex = bandIndex,
                                levelMb = levelMb
                            )
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = AudioEffectsColors.Border
            )

            Spacer(modifier = Modifier.height(18.dp))

            BassBoostCard(
                state = state,
                onEnabledChanged = {
                    onIntent(VideoOptionsIntent.OnBassBoostEnabledChanged(it))
                },
                onStrengthChanged = { strength ->
                    onIntent(VideoOptionsIntent.OnBassBoostStrengthChanged(strength))
                }
            )

            state.errorMessage?.takeIf { it.isNotBlank() }?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    color = AudioEffectsColors.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W700
                )
            }
        }
    }
}

@Composable
private fun AudioEffectsDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 10.dp)
            .width(44.dp)
            .height(5.dp)
            .background(
                color = AudioEffectsColors.Border,
                shape = RoundedCornerShape(100.dp)
            )
    )
}

@Composable
private fun AudioEffectsHeroHeader(
    isAvailable: Boolean,
    isEqualizerEnabled: Boolean,
    isBassBoostEnabled: Boolean,
) {
    val statusText = when {
        !isAvailable -> "Inactive"
        isEqualizerEnabled || isBassBoostEnabled -> "Live tuning"
        else -> "Ready"
    }

    val statusBackground = when {
        !isAvailable -> Color(0xFFF2F4F7)
        isEqualizerEnabled || isBassBoostEnabled -> Color(0xFFFFECEC)
        else -> Color(0xFFF2F4F7)
    }

    val statusColor = when {
        !isAvailable -> AudioEffectsColors.TextSecondary
        isEqualizerEnabled || isBassBoostEnabled -> AudioEffectsColors.Primary
        else -> AudioEffectsColors.TextPrimary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.10f)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFFFF7F7)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = Color(0xFFFFECEC),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_equalizer),
                contentDescription = null,
                tint = AudioEffectsColors.Primary,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.audio_effects_title),
                color = AudioEffectsColors.TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.W900
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Shape your audio with EQ and deep bass",
                color = AudioEffectsColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.W600
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = statusBackground,
                    shape = RoundedCornerShape(100.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = statusText,
                color = statusColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.W900
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String,
) {
    Column {
        Text(
            text = title,
            color = AudioEffectsColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.W900
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = subtitle,
            color = AudioEffectsColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.W600
        )
    }
}

@Composable
private fun PresetChipsRow(
    state: AudioEffectsState,
    onPresetSelected: (Short) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.presets.forEach { preset ->
            val isSelected = state.selectedPresetIndex == preset.index

            AssistChip(
                onClick = {
                    onPresetSelected(preset.index)
                },
                label = {
                    Text(
                        text = preset.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W800
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (isSelected) {
                        AudioEffectsColors.Primary
                    } else {
                        AudioEffectsColors.Card
                    },
                    labelColor = if (isSelected) {
                        Color.White
                    } else {
                        AudioEffectsColors.TextPrimary
                    }
                ),
                border = null
            )
        }
    }
}

@Composable
private fun AudioEffectSwitchCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .background(
                color = AudioEffectsColors.Card,
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = AudioEffectsColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.W900
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = AudioEffectsColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.W600
            )
        }

        Switch(
            checked = checked && enabled,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AudioEffectsColors.Primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1),
                disabledCheckedTrackColor = AudioEffectsColors.Border,
                disabledUncheckedTrackColor = AudioEffectsColors.Border
            )
        )
    }
}

@Composable
private fun ProfessionalEqualizerPanel(
    bands: List<AudioEqualizerBand>,
    enabled: Boolean,
    onBandLevelChanged: (Short, Short) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .background(
                color = AudioEffectsColors.Card,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(top = 14.dp, bottom = 16.dp)
    ) {
        EqualizerScaleHeader(
            enabled = enabled
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            bands.forEach { band ->
                ProfessionalEqualizerBar(
                    band = band,
                    enabled = enabled,
                    onLevelChanged = { level ->
                        onBandLevelChanged(
                            band.index,
                            level
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EqualizerScaleHeader(
    enabled: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (enabled) "Manual tuning enabled" else "Enable equalizer to adjust bands",
            color = if (enabled) AudioEffectsColors.Primary else AudioEffectsColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.W800,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "dB",
            color = AudioEffectsColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.W800
        )
    }
}

@Composable
private fun ProfessionalEqualizerBar(
    band: AudioEqualizerBand,
    enabled: Boolean,
    onLevelChanged: (Short) -> Unit,
) {
    val minLevel = band.minLevelMb.toFloat()
    val maxLevel = band.maxLevelMb.toFloat()
    val currentLevel = band.levelMb.toFloat()

    val targetNormalizedLevel = remember(
        band.levelMb,
        band.minLevelMb,
        band.maxLevelMb
    ) {
        ((currentLevel - minLevel) / (maxLevel - minLevel))
            .coerceIn(0f, 1f)
    }

    val animatedNormalizedLevel by animateFloatAsState(
        targetValue = targetNormalizedLevel,
        animationSpec = tween(durationMillis = 140),
        label = "equalizer_band_${band.index}"
    )

    val zeroLevelFraction = remember(
        band.minLevelMb,
        band.maxLevelMb
    ) {
        ((0f - minLevel) / (maxLevel - minLevel))
            .coerceIn(0f, 1f)
    }

    var barHeightPx by remember { mutableIntStateOf(1) }

    fun calculateLevelFromTouch(yPosition: Float): Short {
        val touchFraction = (1f - (yPosition / barHeightPx.toFloat()))
            .coerceIn(0f, 1f)

        val rawLevel = minLevel + ((maxLevel - minLevel) * touchFraction)

        return rawLevel
            .roundToInt()
            .coerceIn(
                minimumValue = band.minLevelMb.toInt(),
                maximumValue = band.maxLevelMb.toInt()
            )
            .toShort()
    }

    Column(
        modifier = Modifier.widthIn(min = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = band.levelMb.toDbLabel(),
            color = if (enabled) AudioEffectsColors.Primary else Color(0xFF98A2B3),
            fontSize = 11.sp,
            fontWeight = FontWeight.W900
        )

        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .width(42.dp)
                .height(168.dp)
                .onSizeChanged { size ->
                    barHeightPx = size.height.coerceAtLeast(1)
                }
                .pointerInput(
                    band.index,
                    band.minLevelMb,
                    band.maxLevelMb,
                    enabled,
                    barHeightPx
                ) {
                    if (!enabled) return@pointerInput

                    detectDragGestures(
                        onDragStart = { offset ->
                            onLevelChanged(calculateLevelFromTouch(offset.y))
                        },
                        onDrag = { change, _ ->
                            onLevelChanged(calculateLevelFromTouch(change.position.y))
                            change.consume()
                        }
                    )
                }
        ) {
            val centerX = size.width / 2f
            val trackWidth = 11.dp.toPx()
            val thumbRadius = 8.5.dp.toPx()

            val zeroY = size.height * (1f - zeroLevelFraction)
            val currentY = size.height * (1f - animatedNormalizedLevel)

            val gridColor = Color(0xFFE4E7EC)
            val inactiveTrackColor = if (enabled) {
                Color(0xFFE9EDF3)
            } else {
                Color(0xFFF1F5F9)
            }

            val activeBrush = if (enabled) {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF5558),
                        AudioEffectsColors.Primary
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD0D5DD),
                        Color(0xFFCBD5E1)
                    )
                )
            }

            listOf(0.25f, 0.5f, 0.75f).forEach { fraction ->
                val y = size.height * fraction
                drawLine(
                    color = gridColor.copy(alpha = 0.55f),
                    start = Offset(centerX - 16.dp.toPx(), y),
                    end = Offset(centerX + 16.dp.toPx(), y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            drawRoundRect(
                color = inactiveTrackColor,
                topLeft = Offset(
                    x = centerX - trackWidth / 2f,
                    y = 0f
                ),
                size = Size(
                    width = trackWidth,
                    height = size.height
                ),
                cornerRadius = CornerRadius(trackWidth, trackWidth)
            )

            val activeTop = minOf(zeroY, currentY)
            val activeHeight = abs(currentY - zeroY).coerceAtLeast(2f)

            drawRoundRect(
                brush = activeBrush,
                topLeft = Offset(
                    x = centerX - trackWidth / 2f,
                    y = activeTop
                ),
                size = Size(
                    width = trackWidth,
                    height = activeHeight
                ),
                cornerRadius = CornerRadius(trackWidth, trackWidth)
            )

            drawLine(
                color = Color(0xFF101828).copy(alpha = if (enabled) 0.16f else 0.08f),
                start = Offset(centerX - 17.dp.toPx(), zeroY),
                end = Offset(centerX + 17.dp.toPx(), zeroY),
                strokeWidth = 1.2.dp.toPx()
            )

            drawCircle(
                color = Color(0xFF101828).copy(alpha = if (enabled) 0.16f else 0.08f),
                radius = thumbRadius + 3.dp.toPx(),
                center = Offset(centerX, currentY)
            )

            drawCircle(
                brush = activeBrush,
                radius = thumbRadius,
                center = Offset(centerX, currentY)
            )

            drawCircle(
                color = Color.White,
                radius = thumbRadius * 0.42f,
                center = Offset(centerX, currentY)
            )
        }

        Spacer(modifier = Modifier.height(9.dp))

        Text(
            text = band.centerFrequencyHz.toFrequencyLabel(),
            color = AudioEffectsColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.W900
        )
    }
}

@Composable
private fun BassBoostCard(
    state: AudioEffectsState,
    onEnabledChanged: (Boolean) -> Unit,
    onStrengthChanged: (Short) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .background(
                color = AudioEffectsColors.Card,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.audio_effects_bass_boost),
                    color = AudioEffectsColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W900
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (state.isBassBoostSupported) {
                        if (state.isBassStrengthControlSupported) {
                            "Deep bass intensity • ${state.bassStrength}/1000"
                        } else {
                            "Bass available • strength fixed by device"
                        }
                    } else {
                        "Not supported on this device"
                    },
                    color = AudioEffectsColors.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600
                )
            }

            Switch(
                checked = state.isBassBoostEnabled && state.isBassBoostSupported,
                enabled = state.isBassBoostSupported,
                onCheckedChange = onEnabledChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AudioEffectsColors.Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1),
                    disabledCheckedTrackColor = AudioEffectsColors.Border,
                    disabledUncheckedTrackColor = AudioEffectsColors.Border
                )
            )
        }

        if (state.isBassBoostSupported) {
            Spacer(modifier = Modifier.height(10.dp))

            Slider(
                value = state.bassStrength.toFloat(),
                onValueChange = { value ->
                    onStrengthChanged(
                        value.roundToInt()
                            .coerceIn(0, 1000)
                            .toShort()
                    )
                },
                valueRange = 0f..1000f,
                enabled = state.isBassBoostEnabled &&
                        state.isBassStrengthControlSupported,
                colors = SliderDefaults.colors(
                    thumbColor = AudioEffectsColors.Primary,
                    activeTrackColor = AudioEffectsColors.Primary,
                    inactiveTrackColor = AudioEffectsColors.Border,
                    disabledThumbColor = Color(0xFFCBD5E1),
                    disabledActiveTrackColor = AudioEffectsColors.Border,
                    disabledInactiveTrackColor = Color(0xFFF2F4F7)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Soft",
                    color = AudioEffectsColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W700
                )

                Text(
                    text = "Powerful",
                    color = AudioEffectsColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W700
                )
            }
        }
    }
}

@Composable
private fun UnavailableEffectsMessage(
    message: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFECEC),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = message,
            color = AudioEffectsColors.Primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.W800
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Play an audio file first, then open this panel again.",
            color = AudioEffectsColors.TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.W600
        )
    }
}

@Stable
private fun Int.toFrequencyLabel(): String {
    return if (this >= 1000) {
        "${this / 1000}k"
    } else {
        "${this}Hz"
    }
}

@Stable
private fun Short.toDbLabel(): String {
    val dbValue = this / 100f
    val rounded = (dbValue * 10).roundToInt() / 10f

    return if (rounded > 0f) {
        "+${rounded}dB"
    } else {
        "${rounded}dB"
    }
}

private object AudioEffectsColors {
    val Primary = Color(0xFFE00004)
    val Surface = Color.White
    val Card = Color(0xFFF8F9FA)
    val Border = Color(0xFFE9EDF3)
    val TextPrimary = Color(0xFF101828)
    val TextSecondary = Color(0xFF667085)
}