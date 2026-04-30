package com.app.videodownloader.presentation.screens.social.states

import com.app.videodownloader.domain.model.ReelCategory
import com.app.videodownloader.presentation.screens.home.states.SocialSide
import com.app.videodownloader.presentation.screens.home.states.socialSideList

data class SocialStates(
    val isLoading: Boolean = false,
    val category: List<ReelCategory> = emptyList(),
     val error: String? = null,
    val url: String = "",
)
