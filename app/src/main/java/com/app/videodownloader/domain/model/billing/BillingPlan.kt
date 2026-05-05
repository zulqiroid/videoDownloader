package com.app.videodownloader.domain.model.billing

import androidx.annotation.StringRes
import com.app.videodownloader.presentation.screens.premium.state.PlanType

data class BillingPlan(
    val productId: String,
    val planType: PlanType,
    @StringRes val titleRes: Int,
    @StringRes val badgeRes: Int,
    val formattedPrice: String,
    val productType: String,
    val offerToken: String? = null,
    val isAvailable: Boolean = true,
)