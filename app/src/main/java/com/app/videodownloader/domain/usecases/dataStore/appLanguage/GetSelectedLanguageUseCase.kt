package com.app.videodownloader.domain.usecases.dataStore.appLanguage

import com.app.videodownloader.domain.repository.AppLanguageRepository
import com.app.videodownloader.presentation.localization.AppLanguageCodes

class GetSelectedLanguageUseCase(
    private val repository: AppLanguageRepository
) {
    suspend operator fun invoke(): AppLanguageCodes {
        return repository.getSelectedLanguage()
    }
}