package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.componants

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.R

@Composable
fun MediaPermissionRequiredDialog(
    visible: Boolean,
    openSettings: Boolean,
    onAllowClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.media_permission_required_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W800,
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.media_permission_required_message),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (openSettings) {
                            onSettingsClick()
                        } else {
                            onAllowClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE00004),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (openSettings) {
                            stringResource(R.string.media_permission_open_settings_button)
                        } else {
                            stringResource(R.string.media_permission_allow_button)
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W700
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.common_cancel),
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.W600
                    )
                }
            }
        }
    }
}