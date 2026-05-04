package com.app.videodownloader.data.repository.implementation

import com.app.videodownloader.data.local.dataSource.AppLanguageLocalDataSource
 import com.app.videodownloader.domain.repository.AppLanguageRepository
import com.app.videodownloader.presentation.localization.AppLanguageCodes
import kotlinx.coroutines.flow.Flow

class AppLanguageRepositoryImpl(
    private val localDataSource: AppLanguageLocalDataSource
) : AppLanguageRepository {

    override val selectedLanguage: Flow<AppLanguageCodes> =
        localDataSource.selectedLanguage

    override suspend fun getSelectedLanguage(): AppLanguageCodes {
        return localDataSource.getSelectedLanguage()
    }

    override suspend fun saveSelectedLanguage(language: AppLanguageCodes) {
        localDataSource.saveSelectedLanguage(language)
    }

    override suspend fun hasSelectedLanguage(): Boolean {
        return localDataSource.hasSelectedLanguage()
    }
}