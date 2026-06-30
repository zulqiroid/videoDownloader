package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.componants

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.R

@Composable
fun NotificationSettingsDialog(
    visible: Boolean,
    downloadCompleteEnabled: Boolean,
    downloadFailedEnabled: Boolean,
    appUpdatesEnabled: Boolean,
    onDownloadCompleteChanged: (Boolean) -> Unit,
    onDownloadFailedChanged: (Boolean) -> Unit,
    onAppUpdatesChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 24.dp,
                        bottom = 18.dp
                    )
            ) {
                Text(
                    text = stringResource(R.string.notifications_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(18.dp))

                NotificationSettingItem(
                    title = stringResource(R.string.notification_download_complete_title),
                    description = stringResource(R.string.notification_download_complete_description),
                    checked = downloadCompleteEnabled,
                    onCheckedChange = onDownloadCompleteChanged
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0xFFE2E8F0)
                )

                NotificationSettingItem(
                    title = stringResource(R.string.notification_download_failed_title),
                    description = stringResource(R.string.notification_download_failed_description),
                    checked = downloadFailedEnabled,
                    onCheckedChange = onDownloadFailedChanged
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0xFFE2E8F0)
                )

                NotificationSettingItem(
                    title = stringResource(R.string.notification_app_updates_title),
                    description = stringResource(R.string.notification_app_updates_description),
                    checked = appUpdatesEnabled,
                    onCheckedChange = onAppUpdatesChanged
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color(0xFFF1F5F9),
                            contentColor = Color(0xFF475569)
                        ),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.common_cancel),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onSaveClick,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE00004),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.common_save),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onCheckedChange(!checked)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.W500,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFF64748B)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFE00004),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFF1F5F9),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}