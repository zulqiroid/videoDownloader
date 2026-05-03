package com.app.videodownloader.data.local.dataStore.notification

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.app.videodownloader.core.utils.DataStoreKeys.APP_UPDATES_ENABLED
import com.app.videodownloader.core.utils.DataStoreKeys.DOWNLOAD_COMPLETE_ENABLED
import com.app.videodownloader.core.utils.DataStoreKeys.DOWNLOAD_FAILED_ENABLED
import com.app.videodownloader.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationSettingsLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : NotificationSettingsLocalDataSource {

    override fun observeNotificationSettings(): Flow<NotificationSettings> {
        return dataStore.data.map { preferences ->
            NotificationSettings(
                downloadCompleteEnabled = preferences[DOWNLOAD_COMPLETE_ENABLED] ?: true,
                downloadFailedEnabled = preferences[DOWNLOAD_FAILED_ENABLED] ?: true,
                appUpdatesEnabled = preferences[APP_UPDATES_ENABLED] ?: false,
            )
        }
    }

    override suspend fun updateNotificationSettings(settings: NotificationSettings) {
        dataStore.edit { preferences ->
            preferences[DOWNLOAD_COMPLETE_ENABLED] = settings.downloadCompleteEnabled
            preferences[DOWNLOAD_FAILED_ENABLED] = settings.downloadFailedEnabled
            preferences[APP_UPDATES_ENABLED] = settings.appUpdatesEnabled
        }
    }
}