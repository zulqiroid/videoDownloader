package com.app.videodownloader.presentation.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun DownloadStartedDialog(
    isVisible: Boolean,
    onViewProgress: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    Dialog(onDismissRequest = onDismiss) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(30.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 🔴 Icon with soft background
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE00004).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE00004)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                Text(
                    text = "Download Started",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF09090B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = "Your video is now downloading in the background. You can check the status at any time.",
                    fontSize = 14.sp,
                    color = Color(0xFF71717A),
                    textAlign = TextAlign.Center,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.W400
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 🔴 Primary Button
                AppButton(
                    onClick = onViewProgress,
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "View Progress",
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Button
                AppButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Dismiss",
                    containerColor = Color(0xFFF4F4F5),
                    contentColor = Color(0xFF18181B)
                )
            }
        }
    }
}