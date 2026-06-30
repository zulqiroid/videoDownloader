package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.RingtoneTargetType
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.CloseButton

@Composable
fun SetAsRingtoneDialog(
    selectedType: RingtoneTargetType,
    isLoading: Boolean,
    errorMessage: String?,
    onTypeSelected: (RingtoneTargetType) -> Unit,
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        }
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(0.86f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                SetAsRingtoneHeader(
                    dismissEnabled = !isLoading,
                    onDismiss = onDismiss
                )

                Spacer(modifier = Modifier.height(22.dp))

                RingtoneOption.entries.forEachIndexed { index, option ->
                    SetAsRingtoneOptionItem(
                        option = option,
                        selected = selectedType == option.type,
                        enabled = !isLoading,
                        onClick = {
                            onTypeSelected(option.type)
                        }
                    )

                    if (index != RingtoneOption.entries.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = errorMessage,
                        color = Color(0xFFE00004),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onConfirmClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE00004),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE00004).copy(alpha = 0.45f),
                        disabledContentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.confirm_selection),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SetAsRingtoneHeader(
    dismissEnabled: Boolean,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.set_as_ringtone),
            color = Color(0xFF111827),
            fontSize = 18.sp,
            fontWeight = FontWeight.W800,
            modifier = Modifier.weight(1f)
        )

        CloseButton(
            onClick = { onDismiss() },
            modifier = Modifier
        )
    }
}

@Composable
private fun SetAsRingtoneOptionItem(
    option: RingtoneOption,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val activeColor = Color(0xFFE00004)
    val iconTint = if (selected) activeColor else Color(0xFF6B7280)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3F4F6).copy(alpha = 0.3f))
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .dropShadow(
                    shape = CircleShape,
                    shadow = androidx.compose.ui.graphics.shadow.Shadow(
                        radius = 5.dp,
                        spread = 1.dp,
                        color = Color.Black,
                        offset = DpOffset(0.dp, 0.dp)
                    )
                )
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(option.icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.size(14.dp))

        Text(
            text = stringResource(option.titleRes),
            color = Color(0xFF111827),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            modifier = Modifier.weight(1f)
        )

        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = activeColor,
                unselectedColor = Color(0xFFE5E7EB),
                disabledSelectedColor = activeColor.copy(alpha = 0.45f),
                disabledUnselectedColor = Color(0xFFE5E7EB).copy(alpha = 0.45f)
            )
        )
    }
}

@Immutable
private enum class RingtoneOption(
    val type: RingtoneTargetType,
    @StringRes val titleRes: Int,
    val icon: Int,
) {
    DefaultRingtone(
        type = RingtoneTargetType.DefaultRingtone,
        titleRes = R.string.default_ringtone,
        icon = R.drawable.ic_ringing_phone
    ),
    NotificationSound(
        type = RingtoneTargetType.NotificationSound,
        titleRes = R.string.notification_sound,
        icon = R.drawable.ic_bell_filled
    ),
    AlarmTone(
        type = RingtoneTargetType.AlarmTone,
        titleRes = R.string.alarm_tone,
        icon = R.drawable.ic_alarm_clock_filled
    )
}