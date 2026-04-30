package com.app.videodownloader.presentation.screens.main.states

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.domain.model.SocialPlatform

sealed class BottomNavItem(
    val reel: Reel? = null,
     val title: String,
    val iconSelected: Int,
    val iconNotSelected: Int
) {
    data object Home : BottomNavItem(
        title = "Home",
        iconSelected = R.drawable.ic_home_enable,
        iconNotSelected = R.drawable.ic_home_disable,
    )

    data object Player : BottomNavItem(
        title = "Player",
        iconSelected = R.drawable.ic_player_enable,
        iconNotSelected = R.drawable.ic_player_disable,
        )

    data object Reels : BottomNavItem(
        reel= null,
        title = "Reels",
        iconSelected = R.drawable.ic_reel2_filled,
        iconNotSelected = R.drawable.ic_reels2,
        )

    data object Download : BottomNavItem(
        title = "Download",
        iconSelected = R.drawable.ic_download_enable,
        iconNotSelected = R.drawable.ic_download_disable,
        )
    data object More : BottomNavItem(
        title = "More",
        iconSelected = R.drawable.ic_more_filled,
        iconNotSelected = R.drawable.ic_more_outlined,
        )
    data object Social : BottomNavItem(
        title = "Social",
        iconSelected = R.drawable.ic_more_filled,
        iconNotSelected = R.drawable.ic_more_outlined,
        )
}