package com.app.videodownloader.presentation.screens.download.states

import android.content.ContentUris
import android.provider.MediaStore
import com.app.videodownloader.domain.model.DownloadItem
import com.app.videodownloader.domain.model.DownloadStatus
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.presentation.screens.player.states.PlayerUiItem
import com.google.android.gms.ads.nativead.NativeAd


data class DownloadState(
    val isLoading: Boolean = false,
    val selectedTab: DownloadTab = DownloadTab.DOWNLOADING,
    val downloading: List<DownloadUiItem> = emptyList(),
    val completed: List<DownloadUiItem> = emptyList(),
    val error: String? = null,
    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdPools: Map<String, Map<String, NativeAd>> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default(),
)


enum class DownloadTab {
    DOWNLOADING,
    COMPLETED
}

data class DownloadUiItem(
    val id: Long,
    val title: String,
    val progress: Int,
    val status: DownloadStatus,
    val sizeText: String = "",
    val timeText: String = "",
    val filePath: String? = null
)
fun DownloadItem.toUiItem(): DownloadUiItem {

    val totalMB = totalBytes / (1024f * 1024f)
    val formattedSize = String.format("%.2f", totalMB)

    return DownloadUiItem(
        id = id,
        title = fileName,
        progress = progress,
        status = status,
        sizeText = "$formattedSize MB",
        timeText = "Completed",
        filePath = filePath
    )
}

fun DownloadUiItem.toMediaFile(isVideo: Boolean): MediaFile {
    val uri = if (isVideo) {
        ContentUris.withAppendedId(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            id
        )
    } else {
        ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )
    }

    return MediaFile(
        id = id,
        filePath = filePath ?: "",
        fileName = title,
        isVideo = isVideo,
        contentUri = uri.toString()
    )
}