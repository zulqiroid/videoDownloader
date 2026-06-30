package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.billing

import android.app.Activity
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.billing.BillingConnectionState
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.billing.BillingPlan
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.billing.BillingPurchaseResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.state.PlanType
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