package com.app.videodownloader.presentation.screens.appLanguage.componants

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.R

@Composable
fun TopBar(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
    ) {

        Column(
            modifier = modifier.padding(horizontal = 24.dp).padding(bottom = 12.dp)
        ) {
            Text(
                text = "Choose Language",
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "Select your preferred language to continue",
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFF64748B)
            )
        }
    }
}