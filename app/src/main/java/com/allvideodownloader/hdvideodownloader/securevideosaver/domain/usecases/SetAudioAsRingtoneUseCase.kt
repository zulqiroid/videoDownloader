package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.RingtoneTargetType
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.MediaFileRepository

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