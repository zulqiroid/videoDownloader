package com.app.videodownloader.presentation.screens.player.componants

import androidx.compose.foundation.background
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.videodownloader.R

@Composable
fun DeleteFileDialog(
    isLoading: Boolean,
    errorMessage: String?,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = {
            if (!isLoading) onCancelClick()
        }
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = Color(0xFFE00004).copy(alpha = 0.08f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = null,
                        tint = Color(0xFFE00004),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Delete File",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W800,
                    color = Color(0xFF111827),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Are you sure you want to delete this file? This action cannot be undone.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFFE00004),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(34.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onCancelClick,
                        enabled = !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color(0xFFF1F3F6),
                            contentColor = Color(0xFF111827),
                            disabledContainerColor = Color(0xFFF1F3F6).copy(alpha = 0.7f),
                            disabledContentColor = Color(0xFF9CA3AF)
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W800
                        )
                    }

                    Button(
                        onClick = onDeleteClick,
                        enabled = !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
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
                                text = "Delete",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W800
                            )
                        }
                    }
                }
            }
        }
    }
}