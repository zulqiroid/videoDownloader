package com.app.videodownloader.domain.usecases

import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.repository.MediaFileRepository

class DeleteMediaFileUseCase(
    private val repository: MediaFileRepository
) {
    suspend operator fun invoke(
        mediaFile: MediaFile
    ) = repository.deleteMediaFile(mediaFile)
}