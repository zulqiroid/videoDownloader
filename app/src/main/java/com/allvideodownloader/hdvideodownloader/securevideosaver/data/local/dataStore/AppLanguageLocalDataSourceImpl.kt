package com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.allvideodownloader.hdvideodownloader.securevideosaver.core.utils.DataStoreKeys
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataSource.AppLanguageLocalDataSource
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLanguageCodes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppLanguageLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : AppLanguageLocalDataSource {

    override val selectedLanguage: Flow<AppLanguageCodes> =
        dataStore.data.map { preferences ->
            val savedCode = preferences[DataStoreKeys.SELECTED_LANGUAGE_CODE]
            AppLanguageCodes.fromCode(savedCode)
        }

    override suspend fun getSelectedLanguage(): AppLanguageCodes {
        val preferences = dataStore.data.first()
        val savedCode = preferences[DataStoreKeys.SELECTED_LANGUAGE_CODE]
        return AppLanguageCodes.fromCode(savedCode)
    }

    override suspend fun saveSelectedLanguage(language: AppLanguageCodes) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.SELECTED_LANGUAGE_CODE] = language.code
        }
    }

    override suspend fun hasSelectedLanguage(): Boolean {
        val preferences = dataStore.data.first()
        return preferences.contains(DataStoreKeys.SELECTED_LANGUAGE_CODE)
    }
}