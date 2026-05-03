package com.app.videodownloader.presentation.screens.player.states

import android.content.ContentUris
import android.provider.MediaStore
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.google.android.gms.ads.nativead.NativeAd

data class PlayerState(
    val selectedTab: PlayerTab = PlayerTab.VIDEO,
    val videos: List<PlayerUiItem> = emptyList(),
    val audios: List<PlayerUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val nativeAds: Map<String, NativeAd> = emptyMap(),
    val nativeAdConfig: NativeAdConfig = NativeAdConfig.default(),
)

enum class PlayerTab {
    VIDEO,
    AUDIO
}

data class PlayerUiItem(
    val id: Long,
    val title: String,
    val duration: String,
    val size: String,
    val quality: String,
    val filePath: String,
    val date: String
)

fun PlayerUiItem.toMediaFile(isVideo: Boolean): MediaFile {
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
        filePath = filePath,
        fileName = title,
        isVideo = isVideo,
        contentUri = uri.toString()
    )
}