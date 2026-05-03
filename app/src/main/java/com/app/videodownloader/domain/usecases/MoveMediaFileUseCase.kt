package com.app.videodownloader.domain.usecases

import android.net.Uri
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.repository.MediaFileRepository

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