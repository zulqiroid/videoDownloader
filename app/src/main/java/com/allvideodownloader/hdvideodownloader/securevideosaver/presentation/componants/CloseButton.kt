package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = Color(0xFF9CA3AF),
                shape = CircleShape
            )
            .background(Color.White)
            .clickableNoRipple(onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "close",
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun Modifier.clickableNoRipple(
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }

    return clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}
