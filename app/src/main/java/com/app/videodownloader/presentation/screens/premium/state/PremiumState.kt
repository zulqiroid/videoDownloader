package com.app.videodownloader.presentation.screens.premium.state

data class PremiumState(
    val isLoading: Boolean = false,
    val selectedPlan: PlanType = PlanType.MONTHLY,
    val plans: List<Plan> = emptyList(),
    val isPurchaseInProgress: Boolean = false,
    val error: String? = null
)

enum class PlanType(
    val title: String,
    val badge: String
) {
    WEEKLY(
        title = "Weekly",
        badge = "Basic"
    ),
    MONTHLY(
        title = "Monthly",
        badge = "Popular"
    ),
    YEARLY(
        title = "Yearly",
        badge = "Best Choice"
    )
}

data class Plan(
    val type: PlanType,
    val price: String,
    val isPopular: Boolean = false
)