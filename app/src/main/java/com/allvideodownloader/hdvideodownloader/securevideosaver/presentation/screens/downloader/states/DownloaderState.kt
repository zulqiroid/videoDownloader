package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloader.states

import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.dto.DownloadableDto

data class DownloaderState(
    val url: String = "",
    val isLoading: Boolean = false,
    val title: String? = null,
    val thumbnail: String? = null,
    val downloadOptions: List<DownloadableDto> = emptyList(),
    val isDownloading: Boolean = false,
    val error: String? = null
)