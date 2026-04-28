package com.app.videodownloader.presentation.screens.home.states

import com.app.videodownloader.R
import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.domain.model.ReelCategory

data class HomeState(
    val isLoading: Boolean = false,
    val category: List<ReelCategory> = emptyList(),
    val socialSide: List<SocialSide> = socialSideList(),
    val error: String? = null,
    val url: String = "",
)

data class SocialSide(
    val icon: Int,
    val name: String,
)


fun socialSideList() = listOf(
    SocialSide(
        R.drawable.ic_fabkook,
        "Fabkook"
    ),
    SocialSide(
        R.drawable.ic_instudio,
        "Instudio"
    ),
    SocialSide(
        R.drawable.ic_tickotik,
        "TickoTik"
    ),
    SocialSide(
        R.drawable.ic_twetsot,
        "Twetsot"
    ),
    SocialSide(
        R.drawable.ic_talktrand,
        "Talktrand"
    ),
    SocialSide(
        R.drawable.ic_dailymoots,
        "DailyMoots"
    ),
    SocialSide(
        R.drawable.ic_pintests,
        "Pintests"
    ),
    SocialSide(
        R.drawable.ic_liketoo,
        "Liketoo"
    )
)