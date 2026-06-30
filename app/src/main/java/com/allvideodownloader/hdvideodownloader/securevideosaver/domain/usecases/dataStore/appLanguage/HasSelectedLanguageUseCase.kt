package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.appLanguage

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.AppLanguageRepository

class HasSelectedLanguageUseCase(
    private val repository: AppLanguageRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.hasSelectedLanguage()
    }
}