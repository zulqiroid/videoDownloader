    package com.app.videodownloader.presentation.screens.premium.state

data class PremiumState(
    val isLoading: Boolean = false,
    val selectedPlan: PlanType = PlanType.MONTHLY,
    val plans: List<Plan> = emptyList(),
    val isPurchaseInProgress: Boolean = false,
    val error: String? = null
)

enum class PlanType {
    WEEKLY, MONTHLY, YEARLY
}

data class Plan(
    val type: PlanType,
    val price: String,
    val isPopular: Boolean = false
)