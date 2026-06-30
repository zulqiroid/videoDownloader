package com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore.push

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.allvideodownloader.hdvideodownloader.securevideosaver.core.utils.DataStoreKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FcmTokenLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun saveToken(
        token: String
    ) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.FCM_TOKEN] = token
            preferences[DataStoreKeys.IS_TOKEN_SYNCED] = false
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data
            .map { preferences ->
                preferences[DataStoreKeys.FCM_TOKEN]
            }
            .first()
    }

    suspend fun markTokenSynced(
        token: String
    ) {
        dataStore.edit { preferences ->
            if (preferences[DataStoreKeys.FCM_TOKEN] == token) {
                preferences[DataStoreKeys.IS_TOKEN_SYNCED] = true
            }
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(DataStoreKeys.FCM_TOKEN)
            preferences.remove(DataStoreKeys.IS_TOKEN_SYNCED)
        }
    }
}