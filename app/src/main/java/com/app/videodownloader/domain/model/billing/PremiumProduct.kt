package com.app.videodownloader.domain.model.billing

import com.android.billingclient.api.BillingClient
import com.app.videodownloader.presentation.screens.premium.state.PlanType

enum class PremiumProduct(
    val productId: String,
    val productType: String,
    val planType: PlanType,
) {
    WEEKLY(
        productId = "premium_weekly",
        productType = BillingClient.ProductType.SUBS,
        planType = PlanType.WEEKLY
    ),

    MONTHLY(
        productId = "premium_monthly",
        productType = BillingClient.ProductType.SUBS,
        planType = PlanType.MONTHLY
    ),

    YEARLY(
        productId = "premium_yearly",
        productType = BillingClient.ProductType.SUBS,
        planType = PlanType.YEARLY
    ),

    LIFETIME(
        productId = "premium_lifetime",
        productType = BillingClient.ProductType.INAPP,
        planType = PlanType.LIFETIME
    );

    companion object {

        fun fromPlanType(
            planType: PlanType,
        ): PremiumProduct {
            return entries.first { product ->
                product.planType == planType
            }
        }

        fun fromProductId(
            productId: String,
        ): PremiumProduct? {
            return entries.firstOrNull { product ->
                product.productId == productId
            }
        }

        fun subscriptions(): List<PremiumProduct> {
            return entries.filter { product ->
                product.productType == BillingClient.ProductType.SUBS
            }
        }

        fun inApps(): List<PremiumProduct> {
            return entries.filter { product ->
                product.productType == BillingClient.ProductType.INAPP
            }
        }

        fun allProductIds(): Set<String> {
            return entries.map { product ->
                product.productId
            }.toSet()
        }
    }
}