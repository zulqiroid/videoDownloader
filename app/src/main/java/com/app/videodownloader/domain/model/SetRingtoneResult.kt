package com.app.videodownloader.domain.model

sealed interface SetRingtoneResult {

    data object Success : SetRingtoneResult

    data object RequiresWriteSettingsPermission : SetRingtoneResult

    data class Failure(
        val message: String
    ) : SetRingtoneResult
}