package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.appLanguage

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.AppLanguageRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLanguageCodes

class GetSelectedLanguageUseCase(
    private val repository: AppLanguageRepository
) {
    suspend operator fun invoke(): AppLanguageCodes {
        return repository.getSelectedLanguage()
    }
}