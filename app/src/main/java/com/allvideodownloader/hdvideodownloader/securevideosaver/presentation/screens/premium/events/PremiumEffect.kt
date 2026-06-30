package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.events

import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.state.PlanType

sealed interface PremiumEffect {

    data object CloseScreen : PremiumEffect

    data class StartPurchase(
        val plan: PlanType
    ) : PremiumEffect

    data class ShowMessage(
        val message: String
    ) : PremiumEffect

    // ✅ ADD THIS

    data object ShowSuccess : PremiumEffect
}