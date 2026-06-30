package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation

import com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataSource.AppLanguageLocalDataSource
 import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.AppLanguageRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLanguageCodes
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