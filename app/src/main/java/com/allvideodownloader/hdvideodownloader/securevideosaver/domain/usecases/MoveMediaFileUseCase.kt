package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases

import android.net.Uri
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.MediaFileRepository

class MoveMediaFileUseCase(
    private val repository: MediaFileRepository
) {
    suspend operator fun invoke(
        mediaFile: MediaFile,
        destinationTreeUri: Uri
    ) = repository.moveMediaFile(
        mediaFile = mediaFile,
        destinationTreeUri = destinationTreeUri
    )
}