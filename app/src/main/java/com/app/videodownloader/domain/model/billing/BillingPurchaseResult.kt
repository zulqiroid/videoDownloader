package com.app.videodownloader.domain.model.billing

sealed interface BillingPurchaseResult {

    data object Success : BillingPurchaseResult

    data object Cancelled : BillingPurchaseResult

    data object Pending : BillingPurchaseResult

    data object AlreadyOwned : BillingPurchaseResult

    data class Error(
        val message: String,
    ) : BillingPurchaseResult
}