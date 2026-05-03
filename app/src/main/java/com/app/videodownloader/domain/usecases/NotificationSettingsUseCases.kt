package com.app.videodownloader.domain.usecases

import com.app.videodownloader.domain.model.NotificationSettings
import com.app.videodownloader.domain.repository.NotificationSettingsRepository

data class NotificationSettingsUseCases(
    val observeNotificationSettingsUseCase: ObserveNotificationSettingsUseCase,
    val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase,
)

class ObserveNotificationSettingsUseCase(
    private val repository: NotificationSettingsRepository
) {
    operator fun invoke() = repository.observeNotificationSettings()
}
class UpdateNotificationSettingsUseCase(
    private val repository: NotificationSettingsRepository
) {
    suspend operator fun invoke(settings: NotificationSettings) {
        repository.updateNotificationSettings(settings)
    }
}
