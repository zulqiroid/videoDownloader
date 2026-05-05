package com.app.videodownloader.presentation.screens.premium.events

import com.app.videodownloader.presentation.screens.premium.state.PlanType

sealed interface PremiumIntent {

    data object OnScreenStarted : PremiumIntent

    data object OnCloseClicked : PremiumIntent

    data object OnRestoreClicked : PremiumIntent

    data class OnPlanSelected(
        val plan: PlanType
    ) : PremiumIntent

    data object OnUpgradeClicked : PremiumIntent

    data object OnRetry : PremiumIntent
}