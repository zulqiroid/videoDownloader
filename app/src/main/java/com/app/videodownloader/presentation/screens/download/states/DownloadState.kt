package com.app.videodownloader.presentation.screens.download.states

import android.content.ContentUris
import android.provider.MediaStore
import com.app.videodownloader.domain.model.DownloadItem
import com.app.videodownloader.domain.model.DownloadStatus
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.usecases.CancelDownloadUseCase
import com.app.videodownloader.domain.usecases.GetDownloadedFilesUseCase
import com.app.videodownloader.domain.usecases.ObserveDownloadsUseCase
import com.app.videodownloader.domain.usecases.ads.LoadNativeAdUseCase
import com.app.videodownloader.domain.usecases.ads.ObserveNativeAdConfigUseCase
import com.app.videodownloader.presentation.screens.player.states.PlayerUiItem
import com.app.videodownloader.presentation.utils.searchByQuery
import com.google.android.gms.ads.nativead.NativeAd


data class DownloadState(
    val isLoading: Boolean = false,
    val selectedTab: DownloadTab = DownloadTab.DOWNLOADING,
    val downloading: List<DownloadUiItem> = emptyList(),
    val completed: List<DownloadUiItem> = emptyList(),
    val error: String? = null,

    val isPremiumUser: Boolean = false,

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


enum class DownloadSearchFilter {
    ALL,
    DOWNLOADING,
    COMPLETED
}

data class DownloadSearchUiItem(
    val item: DownloadUiItem,
    val tab: DownloadTab,
)

fun DownloadState.currentTabItems(): List<DownloadUiItem> {
    return when (selectedTab) {
        DownloadTab.DOWNLOADING -> downloading
        DownloadTab.COMPLETED -> completed
    }
}

fun DownloadState.searchItems(
    query: String,
    filter: DownloadSearchFilter,
): List<DownloadSearchUiItem> {
    val sourceItems = when (filter) {
        DownloadSearchFilter.ALL -> {
            downloading.map {
                DownloadSearchUiItem(
                    item = it,
                    tab = DownloadTab.DOWNLOADING
                )
            } + completed.map {
                DownloadSearchUiItem(
                    item = it,
                    tab = DownloadTab.COMPLETED
                )
            }
        }

        DownloadSearchFilter.DOWNLOADING -> {
            downloading.map {
                DownloadSearchUiItem(
                    item = it,
                    tab = DownloadTab.DOWNLOADING
                )
            }
        }

        DownloadSearchFilter.COMPLETED -> {
            completed.map {
                DownloadSearchUiItem(
                    item = it,
                    tab = DownloadTab.COMPLETED
                )
            }
        }
    }

    return sourceItems.searchByQuery(
        query = query,
        { it.item.title },
        { it.item.filePath },
        { it.item.sizeText },
        { it.item.timeText },
        { it.item.status.name }
    )
}