package com.app.videodownloader.domain.repository.billing

import com.app.videodownloader.domain.model.billing.PremiumEntitlement
import kotlinx.coroutines.flow.Flow

interface PremiumEntitlementRepository {

    fun observeEntitlement(): Flow<PremiumEntitlement>

    suspend fun updateEntitlement(
        entitlement: PremiumEntitlement,
    )

    suspend fun clearEntitlement()
}