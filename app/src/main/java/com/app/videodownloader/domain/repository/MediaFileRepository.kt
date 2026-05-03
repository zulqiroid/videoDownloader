package com.app.videodownloader.domain.repository

import android.net.Uri
import com.app.videodownloader.domain.model.DeleteMediaFileResult
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.MoveMediaFileResult

import com.app.videodownloader.domain.model.RenameMediaFileResult
import com.app.videodownloader.domain.model.RingtoneTargetType
import com.app.videodownloader.domain.model.SetRingtoneResult

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