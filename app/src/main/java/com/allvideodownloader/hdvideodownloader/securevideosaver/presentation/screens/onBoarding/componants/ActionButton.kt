package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.onBoarding.componants

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.allvideodownloader.hdvideodownloader.securevideosaver.R

@Composable
fun ActionButton(
    isLastPage: Boolean,
    onNext: () -> Unit
) {
    Button(
        onClick = onNext,
        modifier = Modifier
            .width(120.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red
        )
    ) {
        Text(
            text = if (isLastPage) stringResource(R.string.continue_button) else "Next",
            color = Color.White
        )
    }
}