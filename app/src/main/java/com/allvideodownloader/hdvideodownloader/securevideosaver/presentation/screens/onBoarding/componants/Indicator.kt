package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Indicator(current: Int, total: Int) {

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.wrapContentWidth()
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (index == current) 10.dp else 6.dp)
                    .background(
                        if (index == current) Color.Red else Color.LightGray,
                        shape = CircleShape
                    )
            )
        }
    }
}