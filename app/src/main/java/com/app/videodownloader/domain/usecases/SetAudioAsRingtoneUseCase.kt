package com.app.videodownloader.domain.usecases

import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.RingtoneTargetType
import com.app.videodownloader.domain.repository.MediaFileRepository

class SetAudioAsRingtoneUseCase(
    private val repository: MediaFileRepository
) {
    suspend operator fun invoke(
        mediaFile: MediaFile,
        targetType: RingtoneTargetType
    ) = repository.setAudioAsRingtone(
        mediaFile = mediaFile,
        targetType = targetType
    )
}