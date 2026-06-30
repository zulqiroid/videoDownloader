package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.RenameMediaFileResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.MediaFileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.DeleteMediaFileResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MoveMediaFileResult
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.RingtoneTargetType
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.SetRingtoneResult

class MediaFileRepositoryImpl(
    private val context: Context
) : MediaFileRepository {

    override suspend fun renameMediaFile(
        mediaFile: MediaFile,
        newNameWithoutExtension: String
    ): RenameMediaFileResult = withContext(Dispatchers.IO) {
        runCatching {
            val currentFile = File(mediaFile.filePath)
            val sanitizedName = newNameWithoutExtension.trim()

            require(currentFile.exists()) {
                "File does not exist"
            }

            require(sanitizedName.isNotBlank()) {
                "File name cannot be empty"
            }

            require(!sanitizedName.containsInvalidFileNameChars()) {
                "File name contains invalid characters"
            }

            val extension = currentFile.extension
            val newFileName = if (extension.isBlank()) {
                sanitizedName
            } else {
                "$sanitizedName.$extension"
            }

            val targetFile = File(currentFile.parentFile, newFileName)

            require(!targetFile.exists()) {
                "A file with this name already exists"
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                renameUsingMediaStore(
                    mediaFile = mediaFile,
                    newFileName = newFileName,
                    targetFile = targetFile
                )
            } else {
                renameUsingFileApi(
                    mediaFile = mediaFile,
                    currentFile = currentFile,
                    targetFile = targetFile
                )
            }
        }.getOrElse { throwable ->
            RenameMediaFileResult.Failure(
                message = throwable.message ?: "Unable to rename file"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun renameUsingMediaStore(
        mediaFile: MediaFile,
        newFileName: String,
        targetFile: File
    ): RenameMediaFileResult {
        val uri = mediaFile.toMediaStoreUri()

        return try {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName)
            }

            val updatedRows = context.contentResolver.update(
                uri,
                values,
                null,
                null
            )

            if (updatedRows > 0) {
                RenameMediaFileResult.Success(
                    mediaFile = mediaFile.copy(
                        fileName = newFileName,
                        filePath = targetFile.absolutePath
                    )
                )
            } else {
                RenameMediaFileResult.RequiresWritePermission(
                    uri = uri
                )
            }
        } catch (exception: RecoverableSecurityException) {
            RenameMediaFileResult.RequiresWritePermission(
                uri = uri,
                pendingIntent = exception.userAction.actionIntent
            )
        } catch (exception: SecurityException) {
            RenameMediaFileResult.RequiresWritePermission(
                uri = uri
            )
        }
    }

    private fun renameUsingFileApi(
        mediaFile: MediaFile,
        currentFile: File,
        targetFile: File
    ): RenameMediaFileResult {
        val renamed = currentFile.renameTo(targetFile)

        if (!renamed) {
            return RenameMediaFileResult.Failure(
                message = "Unable to rename file"
            )
        }

        MediaScannerConnection.scanFile(
            context,
            arrayOf(targetFile.absolutePath, currentFile.absolutePath),
            null,
            null
        )

        return RenameMediaFileResult.Success(
            mediaFile = mediaFile.copy(
                fileName = targetFile.name,
                filePath = targetFile.absolutePath
            )
        )
    }


    private fun String.containsInvalidFileNameChars(): Boolean {
        val invalidChars = listOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        return any { it in invalidChars }
    }


    override suspend fun deleteMediaFile(
        mediaFile: MediaFile
    ): DeleteMediaFileResult = withContext(Dispatchers.IO) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                deleteUsingMediaStore(mediaFile)
            } else {
                deleteUsingFileApi(mediaFile)
            }
        }.getOrElse { throwable ->
            DeleteMediaFileResult.Failure(
                message = throwable.message ?: "Unable to delete file"
            )
        }
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteUsingMediaStore(
        mediaFile: MediaFile
    ): DeleteMediaFileResult {
        val uri = mediaFile.toMediaStoreUri()

        return try {
            val deletedRows = context.contentResolver.delete(
                uri,
                null,
                null
            )

            if (deletedRows > 0) {
                DeleteMediaFileResult.Success
            } else {
                DeleteMediaFileResult.RequiresDeletePermission(
                    uri = uri
                )
            }
        } catch (exception: RecoverableSecurityException) {
            DeleteMediaFileResult.RequiresDeletePermission(
                uri = uri,
                pendingIntent = exception.userAction.actionIntent
            )
        } catch (exception: SecurityException) {
            DeleteMediaFileResult.RequiresDeletePermission(
                uri = uri
            )
        }
    }

    private fun deleteUsingFileApi(
        mediaFile: MediaFile
    ): DeleteMediaFileResult {
        val file = File(mediaFile.filePath)

        if (!file.exists()) {
            return DeleteMediaFileResult.Success
        }

        val deleted = file.delete()

        if (!deleted) {
            return DeleteMediaFileResult.Failure(
                message = "Unable to delete file"
            )
        }

        MediaScannerConnection.scanFile(
            context,
            arrayOf(mediaFile.filePath),
            null,
            null
        )

        return DeleteMediaFileResult.Success
    }

    private fun MediaFile.toMediaStoreUri(): Uri {
        val collectionUri = if (isVideo) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        return ContentUris.withAppendedId(collectionUri, id)
    }


    override suspend fun moveMediaFile(
        mediaFile: MediaFile,
        destinationTreeUri: Uri
    ): MoveMediaFileResult = withContext(Dispatchers.IO) {
        runCatching {
            val sourceFile = File(mediaFile.filePath)

            require(sourceFile.exists()) {
                "File does not exist"
            }

            val destinationDirectory = DocumentFile.fromTreeUri(
                context,
                destinationTreeUri
            ) ?: return@runCatching MoveMediaFileResult.Failure(
                message = "Unable to access selected folder"
            )

            require(destinationDirectory.canWrite()) {
                "Selected folder is not writable"
            }

            val mimeType = if (mediaFile.isVideo) {
                "video/*"
            } else {
                "audio/*"
            }

            val targetName = createAvailableFileName(
                directory = destinationDirectory,
                originalName = sourceFile.name
            )

            val destinationFile = destinationDirectory.createFile(
                mimeType,
                targetName
            ) ?: return@runCatching MoveMediaFileResult.Failure(
                message = "Unable to create file in selected folder"
            )

            copyFileToDestination(
                sourceFile = sourceFile,
                destinationUri = destinationFile.uri
            )

            MediaScannerConnection.scanFile(
                context,
                arrayOf(destinationFile.uri.toString()),
                null,
                null
            )

            deleteOriginalAfterMove(mediaFile)
        }.getOrElse { throwable ->
            MoveMediaFileResult.Failure(
                message = throwable.message ?: "Unable to move file"
            )
        }
    }

    override suspend fun setAudioAsRingtone(
        mediaFile: MediaFile,
        targetType: RingtoneTargetType
    ): SetRingtoneResult = withContext(Dispatchers.IO) {
        runCatching {
            if (mediaFile.isVideo) {
                return@runCatching SetRingtoneResult.Failure(
                    message = "Only audio files can be set as ringtone"
                )
            }

            if (!Settings.System.canWrite(context)) {
                return@runCatching SetRingtoneResult.RequiresWriteSettingsPermission
            }

            val file = File(mediaFile.filePath)

            if (!file.exists()) {
                return@runCatching SetRingtoneResult.Failure(
                    message = "Audio file does not exist"
                )
            }

            val audioUri = mediaFile.toMediaStoreUri()

            val ringtoneType = when (targetType) {
                RingtoneTargetType.DefaultRingtone -> RingtoneManager.TYPE_RINGTONE
                RingtoneTargetType.NotificationSound -> RingtoneManager.TYPE_NOTIFICATION
                RingtoneTargetType.AlarmTone -> RingtoneManager.TYPE_ALARM
            }

            RingtoneManager.setActualDefaultRingtoneUri(
                context,
                ringtoneType,
                audioUri
            )

            SetRingtoneResult.Success
        }.getOrElse { throwable ->
            SetRingtoneResult.Failure(
                message = throwable.message ?: "Unable to set ringtone"
            )
        }
    }

    private fun copyFileToDestination(
        sourceFile: File,
        destinationUri: Uri
    ) {
        context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
            sourceFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        } ?: error("Unable to write to selected folder")
    }

    private fun deleteOriginalAfterMove(
        mediaFile: MediaFile
    ): MoveMediaFileResult {
        return when (val deleteResult = deleteMediaFileBlocking(mediaFile)) {
            DeleteMediaFileResult.Success -> {
                MoveMediaFileResult.Success(
                    movedFileName = mediaFile.fileName
                )
            }

            is DeleteMediaFileResult.RequiresDeletePermission -> {
                MoveMediaFileResult.RequiresDeletePermission(
                    uri = deleteResult.uri,
                    pendingIntent = deleteResult.pendingIntent
                )
            }

            is DeleteMediaFileResult.Failure -> {
                MoveMediaFileResult.Failure(
                    message = deleteResult.message
                )
            }
        }
    }

    private fun deleteMediaFileBlocking(
        mediaFile: MediaFile
    ): DeleteMediaFileResult {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deleteUsingMediaStore(mediaFile)
        } else {
            deleteUsingFileApi(mediaFile)
        }
    }


    private fun createAvailableFileName(
        directory: DocumentFile,
        originalName: String
    ): String {
        val cleanName = originalName.ifBlank { "media_file" }

        if (directory.findFile(cleanName) == null) {
            return cleanName
        }

        val extension = cleanName.substringAfterLast('.', "")
        val nameWithoutExtension = cleanName.substringBeforeLast('.', cleanName)

        var index = 1

        while (true) {
            val candidate = if (extension.isBlank()) {
                "$nameWithoutExtension ($index)"
            } else {
                "$nameWithoutExtension ($index).$extension"
            }

            if (directory.findFile(candidate) == null) {
                return candidate
            }

            index++
        }
    }


}