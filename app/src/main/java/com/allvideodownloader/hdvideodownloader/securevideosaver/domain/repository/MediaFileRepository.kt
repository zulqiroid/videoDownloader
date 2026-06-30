package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository

import android.net.Uri
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.DeleteMediaFileResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MoveMediaFileResult

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.RenameMediaFileResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.RingtoneTargetType
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.SetRingtoneResult

interface MediaFileRepository {

    suspend fun renameMediaFile(
        mediaFile: MediaFile,
        newNameWithoutExtension: String
    ): RenameMediaFileResult


    suspend fun deleteMediaFile(
        mediaFile: MediaFile
    ): DeleteMediaFileResult

    suspend fun moveMediaFile(
        mediaFile: MediaFile,
        destinationTreeUri: Uri
    ): MoveMediaFileResult

    suspend fun setAudioAsRingtone(
        mediaFile: MediaFile,
        targetType: RingtoneTargetType
    ): SetRingtoneResult
}