package com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataSource

import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLanguageCodes
import kotlinx.coroutines.flow.Flow

interface AppLanguageLocalDataSource {

    val selectedLanguage: Flow<AppLanguageCodes>

    suspend fun getSelectedLanguage(): AppLanguageCodes

    suspend fun saveSelectedLanguage(language: AppLanguageCodes)

    suspend fun hasSelectedLanguage(): Boolean
}