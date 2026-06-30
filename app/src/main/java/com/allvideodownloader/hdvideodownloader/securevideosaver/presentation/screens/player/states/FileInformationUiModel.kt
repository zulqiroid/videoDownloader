package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states

import android.media.MediaMetadataRetriever
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import java.io.File
import java.util.Locale

  data class FileInformationUiModel(
    val fileName: String,
    val format: String,
    val resolution: String,
    val duration: String,
    val fileSize: String,
    val storageLocation: String,
    val isVideo: Boolean,
)

 fun MediaFile.toFileInformationUiModel(): FileInformationUiModel {
    val file = File(filePath)

    val metadata = readMediaMetadataSafely(filePath)

    return FileInformationUiModel(
        fileName = fileName.ifBlank {
            file.name.ifBlank { "Unknown file" }
        },
        format = file.extension.toDisplayFormat(isVideo),
        resolution = if (isVideo) metadata.resolution else "Audio",
        duration = metadata.duration,
        fileSize = file.length().toReadableFileSize(),
        storageLocation = file.parent.orEmpty().ifBlank { filePath },
        isVideo = isVideo
    )
}

 data class MediaMetadata(
    val duration: String = "--:--",
    val resolution: String = "Unknown",
)

 fun readMediaMetadataSafely(
    path: String,
): MediaMetadata {
    return runCatching {
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(path)

            val durationMs = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
                ?: 0L

            val width = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toIntOrNull()

            val height = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toIntOrNull()

            MediaMetadata(
                duration = durationMs.toReadableDuration(),
                resolution = if (width != null && height != null && width > 0 && height > 0) {
                    "$width × $height"
                } else {
                    "Unknown"
                }
            )
        } finally {
            retriever.release()
        }
    }.getOrDefault(MediaMetadata())
}

 fun String.toDisplayFormat(
    isVideo: Boolean,
): String {
    val extension = lowercase(Locale.getDefault())

    return when {
        extension.isBlank() -> "Unknown"
        isVideo -> "${extension.uppercase(Locale.getDefault())} Video"
        else -> "${extension.uppercase(Locale.getDefault())} Audio"
    }
}

 fun Long.toReadableFileSize(): String {
    if (this <= 0L) return "Unknown"

    val kb = this / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1 -> String.format(Locale.getDefault(), "%.2f GB", gb)
        mb >= 1 -> String.format(Locale.getDefault(), "%.2f MB", mb)
        kb >= 1 -> String.format(Locale.getDefault(), "%.2f KB", kb)
        else -> "$this B"
    }
}

 fun Long.toReadableDuration(): String {
    if (this <= 0L) return "--:--"

    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}