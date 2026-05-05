package com.app.videodownloader.data.repository.implementation.billing

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.app.videodownloader.core.utils.DataStoreKeys
import com.app.videodownloader.domain.model.billing.PremiumEntitlement
import com.app.videodownloader.domain.model.billing.PremiumEntitlementSource
import com.app.videodownloader.domain.repository.billing.PremiumEntitlementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PremiumEntitlementRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : PremiumEntitlementRepository {

    override fun observeEntitlement(): Flow<PremiumEntitlement> {
        return dataStore.data.map { preferences ->
            val isPremium = preferences[DataStoreKeys.IS_PREMIUM_USER] ?: false

            val activeProductIds = preferences[DataStoreKeys.PREMIUM_ACTIVE_PRODUCT_IDS]
                .orEmpty()
                .toProductIdSet()

            val source = preferences[DataStoreKeys.PREMIUM_ENTITLEMENT_SOURCE]
                .orEmpty()
                .toPremiumEntitlementSource()

            val updatedAtMillis = preferences[DataStoreKeys.PREMIUM_ENTITLEMENT_UPDATED_AT]
                ?.toLongOrNull()
                ?: 0L

            PremiumEntitlement(
                isPremium = isPremium,
                activeProductIds = activeProductIds,
                source = source,
                updatedAtMillis = updatedAtMillis
            )
        }
    }

    override suspend fun updateEntitlement(
        entitlement: PremiumEntitlement,
    ) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.IS_PREMIUM_USER] = entitlement.isPremium
            preferences[DataStoreKeys.PREMIUM_ACTIVE_PRODUCT_IDS] =
                entitlement.activeProductIds.toProductIdStorageString()
            preferences[DataStoreKeys.PREMIUM_ENTITLEMENT_SOURCE] =
                entitlement.source.name
            preferences[DataStoreKeys.PREMIUM_ENTITLEMENT_UPDATED_AT] =
                entitlement.updatedAtMillis.toString()
        }
    }

    override suspend fun clearEntitlement() {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.IS_PREMIUM_USER] = false
            preferences[DataStoreKeys.PREMIUM_ACTIVE_PRODUCT_IDS] = ""
            preferences[DataStoreKeys.PREMIUM_ENTITLEMENT_SOURCE] =
                PremiumEntitlementSource.None.name
            preferences[DataStoreKeys.PREMIUM_ENTITLEMENT_UPDATED_AT] =
                System.currentTimeMillis().toString()
        }
    }

    private fun String.toProductIdSet(): Set<String> {
        return split(PRODUCT_ID_SEPARATOR)
            .map { value -> value.trim() }
            .filter { value -> value.isNotBlank() }
            .toSet()
    }

    private fun Set<String>.toProductIdStorageString(): String {
        return joinToString(PRODUCT_ID_SEPARATOR)
    }

    private fun String.toPremiumEntitlementSource(): PremiumEntitlementSource {
        return runCatching {
            PremiumEntitlementSource.valueOf(this)
        }.getOrDefault(PremiumEntitlementSource.None)
    }

    private companion object {
        private const val PRODUCT_ID_SEPARATOR = ","
    }
}