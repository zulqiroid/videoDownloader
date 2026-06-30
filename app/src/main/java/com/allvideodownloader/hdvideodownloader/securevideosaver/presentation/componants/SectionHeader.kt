package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allvideodownloader.hdvideodownloader.securevideosaver.R

/*@Composable
fun SectionHeader(
    text: String,
    supportingText: String = stringResource(R.string.common_see_all),
    onReelSeeAllCLicked: () -> Unit
) {
    SectionHeaderContent(
        text = text,
        supportingText = supportingText,
        onReelSeeAllCLicked = {
            onReelSeeAllCLicked()
        }
    )
}*/

@Composable
fun SectionHeader(
    @StringRes textRes: Int,
    @StringRes supportingTextRes: Int = R.string.common_see_all,
    onReelSeeAllCLicked: () -> Unit
) {
    SectionHeaderContent(
        text = stringResource(textRes),
        supportingText = stringResource(supportingTextRes),
        onReelSeeAllCLicked = {
            onReelSeeAllCLicked()
        }
    )
}

@Composable
private fun SectionHeaderContent(
    text: String,
    supportingText: String,
    onReelSeeAllCLicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.W700
        )

        Row(
            Modifier.clickable{
                onReelSeeAllCLicked()
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = supportingText,
                fontSize = 16.sp,
                color = Color(0xFFE00004),
                fontWeight = FontWeight.W600
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.cd_arrow_forward),
                tint = Color(0xFFE00004),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}