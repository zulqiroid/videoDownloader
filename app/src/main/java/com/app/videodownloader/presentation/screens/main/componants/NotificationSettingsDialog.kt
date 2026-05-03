package com.app.videodownloader.presentation.screens.main.componants

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.presentation.componants.DownloadStartedDialog
import com.app.videodownloader.presentation.componants.exitConfirmationDialogue.ExitConfirmationDialog
import com.app.videodownloader.presentation.componants.privacyPolicyDialgue.PrivacyDialogHost
import com.app.videodownloader.presentation.navigation.Screen
import com.app.videodownloader.presentation.navigation.Screen.*
import com.app.videodownloader.presentation.screens.download.screen.DownloadScreen
import com.app.videodownloader.presentation.screens.home.screen.HomeScreen
import com.app.videodownloader.presentation.screens.main.componants.DownloadBottomSheet
import com.app.videodownloader.presentation.screens.main.componants.FetchingDialog
import com.app.videodownloader.presentation.screens.main.componants.MainBottomBar
import com.app.videodownloader.presentation.screens.main.componants.TopBar
import com.app.videodownloader.presentation.screens.main.events.MainEvents
import com.app.videodownloader.presentation.screens.main.events.MainEvents.*
import com.app.videodownloader.presentation.screens.main.events.MainNavEvents
import com.app.videodownloader.presentation.screens.main.states.BottomNavItem
import com.app.videodownloader.presentation.screens.main.states.DownloadSheetState
import com.app.videodownloader.presentation.screens.main.viewModel.MainViewModel
import com.app.videodownloader.presentation.screens.more.screen.MoreScreen
import com.app.videodownloader.presentation.screens.player.componants.FileOptionsDialog
import com.app.videodownloader.presentation.screens.main.events.FileDialogIntent
import com.app.videodownloader.presentation.screens.main.events.NotificationEvents
import com.app.videodownloader.presentation.screens.player.screen.PlayerScreen
import com.app.videodownloader.presentation.screens.reels.screen.ReelsScreen
import com.app.videodownloader.presentation.screens.social.screen.Social
import org.koin.compose.viewmodel.koinViewModel

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
                    text = "Notifications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(18.dp))

                NotificationSettingItem(
                    title = "Download Complete",
                    description = "Alert when video is ready",
                    checked = downloadCompleteEnabled,
                    onCheckedChange = onDownloadCompleteChanged
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0xFFE2E8F0)
                )

                NotificationSettingItem(
                    title = "Download Failed",
                    description = "Alert on network errors",
                    checked = downloadFailedEnabled,
                    onCheckedChange = onDownloadFailedChanged
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0xFFE2E8F0)
                )

                NotificationSettingItem(
                    title = "App Updates",
                    description = "Get notified about new features",
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
                            text = "Cancel",
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
                            text = "Save",
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