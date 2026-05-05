package com.app.videodownloader.presentation.screens.premium.events

import com.app.videodownloader.presentation.screens.premium.state.PlanType

sealed interface PremiumEffect {

    data object CloseScreen : PremiumEffect

    data class StartPurchase(
        val plan: PlanType
    ) : PremiumEffect

    data class ShowMessage(
        val message: String
    ) : PremiumEffect

    data object ShowSuccess : PremiumEffect
}