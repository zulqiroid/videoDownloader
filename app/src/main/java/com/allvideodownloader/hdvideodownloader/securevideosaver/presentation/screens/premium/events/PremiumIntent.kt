package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.events

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.state.PlanType

sealed interface PremiumIntent {

    data object OnScreenStarted : PremiumIntent

    data class OnCloseClicked(
        val activity: Activity
    ): PremiumIntent

    data object OnRestoreClicked : PremiumIntent

    data class OnPlanSelected(
        val plan: PlanType
    ) : PremiumIntent

    data object OnUpgradeClicked : PremiumIntent

    data object OnRetry : PremiumIntent

    // ✅ ADD THIS
    object OnAdDismissed : PremiumIntent
}