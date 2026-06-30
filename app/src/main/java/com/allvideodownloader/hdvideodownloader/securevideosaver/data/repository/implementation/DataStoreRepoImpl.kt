package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation

import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.AppPreferences
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.DataStoreRepository

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