package com.app.videodownloader.domain.repository

import com.app.videodownloader.presentation.localization.AppLanguageCodes
import kotlinx.coroutines.flow.Flow

interface AppLanguageRepository {

    val selectedLanguage: Flow<AppLanguageCodes>

    suspend fun getSelectedLanguage(): AppLanguageCodes

    suspend fun saveSelectedLanguage(language: AppLanguageCodes)

    suspend fun hasSelectedLanguage(): Boolean
}