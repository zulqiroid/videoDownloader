package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {},
    isEnable: Boolean = true,
    fontSIze: Int = 22,
    buttonHeight: Int = 60,
    padding: Int = 16,
    containerColor: Color = Color(0xFFE60000),
    contentColor: Color = Color.White,
) {
    Button(
        onClick = {
            onClick()
        },
        enabled = isEnable,
        modifier = modifier
            .height(buttonHeight.dp)
            .padding(horizontal = padding.dp),
        shape = RoundedCornerShape(15.dp), // fully rounded
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor, // red color
            contentColor = contentColor,
            disabledContainerColor = Color(0xFFDEDDDD),
            disabledContentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Text(
            text = text,
             fontSize = fontSIze.sp,
            fontWeight = FontWeight.W700
        )
    }
}