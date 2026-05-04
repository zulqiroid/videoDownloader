package com.app.videodownloader.domain.usecases.dataStore.appLanguage

import com.app.videodownloader.domain.repository.AppLanguageRepository

class HasSelectedLanguageUseCase(
    private val repository: AppLanguageRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.hasSelectedLanguage()
    }
}