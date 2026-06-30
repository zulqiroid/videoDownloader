package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.states

data class DownloadSheetState(
    val isVisible: Boolean = false,
    val title: String? = null,
    val thumbnail: String? = null,
    val source: String? = null,
    val options: List<DownloadOptionUi> = defaultValue(),
    val selectedIndex: Int = 0
)

data class DownloadOptionUi(
    val quality: String,
    val format: String,
     val isPro: Boolean = false,
    val isMP3: Boolean = false,
)

fun defaultValue() = listOf(

    DownloadOptionUi(
        quality = "1080p (HD)",
        format = "MP4",
         isPro = true
    ),
    DownloadOptionUi(
        quality = "720p",
        format = "MP4",
         isPro = false
    ),
    DownloadOptionUi(
        quality = "480p",
        format = "MP4",
         isPro = false
    ),
 /*   DownloadOptionUi(
        quality = "Audio only",
        format = "MP3",
         isPro = false,
        isMP3 = true
    ),*/

)