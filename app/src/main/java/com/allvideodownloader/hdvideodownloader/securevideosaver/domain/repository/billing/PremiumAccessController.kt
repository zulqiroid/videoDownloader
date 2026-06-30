package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.billing

import kotlinx.coroutines.flow.StateFlow

interface PremiumAccessController {
    val isPremiumUser: StateFlow<Boolean>

    fun isPremium(): Boolean
}