package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.exitConfirmationDialogue

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// ExitConfirmationDialog.kt
@Composable
fun ExitConfirmationDialog(
    visible: Boolean,
    appName: String = "Video Downloader",
    onExitClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onCancelClick
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 18.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExitIcon()

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Exit App?",
                    color = Color(0xFF111827),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Are you sure you want to close ")

                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF667085)
                            )
                        ) {
                            append(appName)
                        }

                        append("? Any ongoing tasks may be paused.")
                    },
                    color = Color(0xFF7A869A),
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                DestructiveButton(
                    text = "Exit",
                    onClick = onExitClick
                )

                Spacer(modifier = Modifier.height(10.dp))

                SecondaryButton(
                    text = "Cancel",
                    onClick = onCancelClick
                )
            }
        }
    }
}
