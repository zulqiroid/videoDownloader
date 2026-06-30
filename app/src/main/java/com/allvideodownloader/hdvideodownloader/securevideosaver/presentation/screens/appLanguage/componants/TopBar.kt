package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.componants

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allvideodownloader.hdvideodownloader.securevideosaver.R

@Composable
fun TopBar(
    modifier: Modifier = Modifier
) {
    Box {
        Column(
            modifier = modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.choose_language_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFF0F172A)
            )

            Text(
                text = stringResource(R.string.choose_language_subtitle),
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFF64748B)
            )
        }
    }
}