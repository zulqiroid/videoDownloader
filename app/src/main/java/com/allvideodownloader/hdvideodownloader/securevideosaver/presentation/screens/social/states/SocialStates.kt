package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.social.states

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ReelCategory

data class SocialStates(
    val isLoading: Boolean = false,
    val category: List<ReelCategory> = emptyList(),
     val error: String? = null,
    val url: String = "",
)
