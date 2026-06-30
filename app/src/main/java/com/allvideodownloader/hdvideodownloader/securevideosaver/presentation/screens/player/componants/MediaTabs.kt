package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states.PlayerTab


@Composable
fun MediaTabs(
    selectedTab: PlayerTab,
    onTabChange: (PlayerTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F3F5), RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {

        TabItem(
            title = stringResource(R.string.videos),
            headingIcon = if (selectedTab == PlayerTab.VIDEO)
                R.drawable.ic_vd_bundle_filled
            else R.drawable.ic_vd_bundle_outlined,
            selected = selectedTab == PlayerTab.VIDEO
        ) {
            onTabChange(PlayerTab.VIDEO)
        }

        TabItem(
            title = stringResource(R.string.music),
            headingIcon = if (selectedTab == PlayerTab.AUDIO)
                R.drawable.ic_audio
            else R.drawable.ic_music_outlined,
            selected = selectedTab == PlayerTab.AUDIO
        ) {
            onTabChange(PlayerTab.AUDIO)
        }
    }
}
@Composable
private fun RowScope.TabItem(
    title: String,
    headingIcon: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            Icon(
                painter = painterResource(headingIcon),
                contentDescription = title,
                tint = if (selected) Color(0xFFE00004) else Color(0xFF868E96),
                modifier = Modifier.size(22.dp)
            )

            Text(
                text = title,
                color = if (selected) Color(0xFFE00004) else Color(0xFF868E96),
                fontWeight = FontWeight.W700,
                fontSize = 14.sp
            )
        }

    }
}