package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states.PlayerState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states.PlayerTab
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.viewModel.PlayerViewModel


@Composable
fun PlayAllButton(
    state: PlayerState,
    viewModel: PlayerViewModel,
    onClick:() -> Unit
) {

    val size = if(state.selectedTab == PlayerTab.VIDEO){
        state.videos.size
    }else{
        state.audios.size
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE00004))
            .clickable {
                onClick()
             }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = stringResource(R.string.play_all_count, size),
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}