package com.app.videodownloader.presentation.screens.main.componants

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.videodownloader.R

@Composable
fun MovingFileDialog(
    visible: Boolean,
    errorMessage: String?,
) {
    if (!visible && errorMessage == null) return

    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (visible) {
                    CircularProgressIndicator(
                        color = Color(0xFFE00004)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.moving_file_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W800,
                        color = Color(0xFF111827)
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFE00004),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}