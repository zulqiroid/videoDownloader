package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.state

import androidx.annotation.StringRes
import com.allvideodownloader.hdvideodownloader.securevideosaver.R

data class PremiumState(
    val isLoading: Boolean = false,
    val selectedPlan: PlanType = PlanType.MONTHLY,
    val plans: List<Plan> = defaultPreviewPlans(),
    val isPurchaseInProgress: Boolean = false,
    val isPremiumUser: Boolean = false,
    val error: String? = null,
    val message: String? = null,
)

enum class PlanType(
    @StringRes val titleRes: Int,
    @StringRes val badgeRes: Int
) {
    WEEKLY(
        titleRes = R.string.plan_weekly,
        badgeRes = R.string.plan_badge_basic
    ),
    MONTHLY(
        titleRes = R.string.plan_monthly,
        badgeRes = R.string.plan_badge_popular
    ),
    YEARLY(
        titleRes = R.string.plan_yearly,
        badgeRes = R.string.plan_badge_best_choice
    ),
    LIFETIME(
        titleRes = R.string.plan_lifetime,
        badgeRes = R.string.plan_badge_best_ever
    )
}

data class Plan(
    val type: PlanType,
    val price: String,
    val isPopular: Boolean = false
)

fun defaultPreviewPlans(): List<Plan> {
    return listOf(
        Plan(
            type = PlanType.WEEKLY,
            price = "--"
        ),
        Plan(
            type = PlanType.MONTHLY,
            price = "--",
            isPopular = true
        ),
        Plan(
            type = PlanType.YEARLY,
            price = "--"
        ),
        Plan(
            type = PlanType.LIFETIME,
            price = "--"
        )
    )
}