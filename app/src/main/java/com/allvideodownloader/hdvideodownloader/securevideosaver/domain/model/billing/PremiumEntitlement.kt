package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.billing

data class PremiumEntitlement(
    val isPremium: Boolean = false,
    val activeProductIds: Set<String> = emptySet(),
    val source: PremiumEntitlementSource = PremiumEntitlementSource.None,
    val updatedAtMillis: Long = 0L,
)

enum class PremiumEntitlementSource {
    None,
    GooglePlayPurchase,
    RestoredPurchase,
}