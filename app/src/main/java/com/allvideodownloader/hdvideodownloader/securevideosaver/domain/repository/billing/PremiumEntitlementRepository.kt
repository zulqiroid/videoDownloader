package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.billing

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.billing.PremiumEntitlement
import kotlinx.coroutines.flow.Flow

interface PremiumEntitlementRepository {

    fun observeEntitlement(): Flow<PremiumEntitlement>

    suspend fun updateEntitlement(
        entitlement: PremiumEntitlement,
    )

    suspend fun clearEntitlement()
}