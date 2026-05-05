package com.app.videodownloader.presentation.screens.main.states

import androidx.annotation.StringRes
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.Reel

sealed class BottomNavItem(
    val reel: Reel? = null,
    @StringRes val titleRes: Int,
    val iconSelected: Int,
    val iconNotSelected: Int
) {
    data object Home : BottomNavItem(
        titleRes = R.string.bottom_nav_home,
        iconSelected = R.drawable.ic_home_enable,
        iconNotSelected = R.drawable.ic_home_disable,
    )

    data object Player : BottomNavItem(
        titleRes = R.string.bottom_nav_player,
        iconSelected = R.drawable.ic_player_enable,
        iconNotSelected = R.drawable.ic_player_disable,
    )

    data object Reels : BottomNavItem(
        reel = null,
        titleRes = R.string.bottom_nav_reels,
        iconSelected = R.drawable.ic_reel2_filled,
        iconNotSelected = R.drawable.ic_reels2,
    )

    data object Download : BottomNavItem(
        titleRes = R.string.bottom_nav_download,
        iconSelected = R.drawable.ic_download_enable,
        iconNotSelected = R.drawable.ic_download_disable,
    )

    data object More : BottomNavItem(
        titleRes = R.string.bottom_nav_more,
        iconSelected = R.drawable.ic_more_filled,
        iconNotSelected = R.drawable.ic_more_outlined,
    )

    data object Social : BottomNavItem(
        titleRes = R.string.bottom_nav_social,
        iconSelected = R.drawable.ic_more_filled,
        iconNotSelected = R.drawable.ic_more_outlined,
    )
}