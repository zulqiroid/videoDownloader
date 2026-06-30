package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.MediaFileRepository

class RenameMediaFileUseCase(
    private val repository: MediaFileRepository
) {
    suspend operator fun invoke(
        mediaFile: MediaFile,
        newNameWithoutExtension: String
    ) = repository.renameMediaFile(
        mediaFile = mediaFile,
        newNameWithoutExtension = newNameWithoutExtension
    )
}