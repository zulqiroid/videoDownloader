package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.dataStore.appLanguage

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.AppLanguageRepository
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLanguageCodes
import kotlinx.coroutines.flow.Flow

class ObserveSelectedLanguageUseCase(
    private val repository: AppLanguageRepository
) {
    operator fun invoke(): Flow<AppLanguageCodes> {
        return repository.selectedLanguage
    }
}