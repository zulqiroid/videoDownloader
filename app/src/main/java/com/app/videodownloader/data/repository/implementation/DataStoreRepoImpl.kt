package com.app.videodownloader.data.repository.implementation

import com.app.videodownloader.data.local.dataStore.AppPreferences
import com.app.videodownloader.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class DataStoreRepoImpl(
    private val preferences: AppPreferences
): DataStoreRepository {

    override val isFirstLaunch = preferences.isFirstLaunch

    override suspend fun setFirstLaunch(value: Boolean) {
        preferences.setFirstLaunch(value)
    }

    override val isPolicyAccepted = preferences.isPolicyAccepted

    override suspend fun setPolicyAccepted(value: Boolean) {
        preferences.setPolicyAccepted(value)
    }
}