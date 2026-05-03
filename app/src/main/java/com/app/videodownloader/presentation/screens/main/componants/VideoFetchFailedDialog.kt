package com.app.videodownloader.presentation.screens.main.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.videodownloader.R

@Composable
fun VideoFetchFailedDialog(
    errorMessage: String?,
    onRetryClick: () -> Unit,
    onPasteNewLinkClick: () -> Unit,
    onHelpClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 30.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFE00004).copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_failed_fetching),
                        contentDescription = null,
                        tint = Color(0xFFE00004),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                Text(
                    text = "Video Fetch Failed",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = errorMessage?.takeIf { it.isNotBlank() }
                        ?: "We couldn’t fetch this video. Please check the link or try another source.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE00004),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Retry",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onPasteNewLinkClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color(0xFFF1F4F8),
                        contentColor = Color(0xFF374151)
                    )
                ) {
                    Text(
                        text = "Paste New Link",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Help",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFFE00004),
                    modifier = Modifier.clickable(onClick = onHelpClick)
                )
            }
        }
    }
}

