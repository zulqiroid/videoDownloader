package com.app.videodownloader.domain.usecases.dataStore.appLanguage

import com.app.videodownloader.domain.repository.AppLanguageRepository
import com.app.videodownloader.presentation.localization.AppLanguageCodes
import kotlinx.coroutines.flow.Flow

class ObserveSelectedLanguageUseCase(
    private val repository: AppLanguageRepository
) {
    operator fun invoke(): Flow<AppLanguageCodes> {
        return repository.selectedLanguage
    }
}