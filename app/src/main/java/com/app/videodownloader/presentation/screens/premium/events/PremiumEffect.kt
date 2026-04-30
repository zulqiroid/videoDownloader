package com.app.videodownloader.presentation.screens.premium.events

import com.app.videodownloader.presentation.screens.premium.state.PlanType

sealed interface PremiumEffect {
    data object CloseScreen : PremiumEffect
    data object RestorePurchases : PremiumEffect

    data class StartPurchase(val plan: PlanType) : PremiumEffect
    data object ShowSuccess : PremiumEffect
    data class ShowError(val message: String) : PremiumEffect
}