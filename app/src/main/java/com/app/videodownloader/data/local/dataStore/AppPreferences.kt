package com.app.videodownloader.data.local.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.app.videodownloader.core.utils.DataStoreKeys.IS_FIRST_LAUNCH
import com.app.videodownloader.core.utils.DataStoreKeys.IS_POLICY_ACCEPTED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(
    private val dataStore: DataStore<Preferences>
) {

    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_FIRST_LAUNCH] ?: false
    }

    suspend fun setFirstLaunch(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_FIRST_LAUNCH] = value
        }
    }

    val isPolicyAccepted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_POLICY_ACCEPTED] ?: false
    }

    suspend fun setPolicyAccepted(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_POLICY_ACCEPTED] = value
        }
    }

}