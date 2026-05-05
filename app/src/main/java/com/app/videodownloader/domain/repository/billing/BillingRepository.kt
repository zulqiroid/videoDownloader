package com.app.videodownloader.domain.repository.billing

import android.app.Activity
import com.app.videodownloader.domain.model.billing.BillingConnectionState
import com.app.videodownloader.domain.model.billing.BillingPlan
import com.app.videodownloader.domain.model.billing.BillingPurchaseResult
import com.app.videodownloader.presentation.screens.premium.state.PlanType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BillingRepository {

    val connectionState: StateFlow<BillingConnectionState>

    val plans: StateFlow<List<BillingPlan>>

    val purchaseResults: Flow<BillingPurchaseResult>

    fun connect()

    fun disconnect()

    fun queryProducts()

    fun launchPurchase(
        activity: Activity,
        planType: PlanType,
    )

    fun restorePurchases()
}