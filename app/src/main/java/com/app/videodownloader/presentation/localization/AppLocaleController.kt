package com.app.videodownloader.presentation.localization

import com.app.videodownloader.domain.usecases.dataStore.appLanguage.ObserveSelectedLanguageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AppLocaleController(
    observeSelectedLanguage: ObserveSelectedLanguageUseCase,
    applicationScope: CoroutineScope
) {
    val selectedLanguage: StateFlow<AppLanguageCodes> =
        observeSelectedLanguage()
            .stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = AppLanguageCodes.DEFAULT
            )
}