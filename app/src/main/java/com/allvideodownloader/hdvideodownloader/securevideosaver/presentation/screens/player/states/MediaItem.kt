package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states

    import android.content.ContentUris
    import android.provider.MediaStore
    import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
    import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
    import com.google.android.gms.ads.nativead.NativeAd

    data class PlayerState(
        val selectedTab: PlayerTab = PlayerTab.VIDEO,
        val videos: List<PlayerUiItem> = emptyList(),
        val audios: List<PlayerUiItem> = emptyList(),
        val isLoading: Boolean = false,

        val isPremiumUser: Boolean = false,
        val nativeAds: Map<String, NativeAd> = emptyMap(),
        val nativeAdConfig: NativeAdConfig = NativeAdConfig.default(),
    )

    enum class PlayerTab {
        VIDEO,
        AUDIO
    }

    enum class PlayerSearchFilter {
        ALL_TYPES,
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

    data class PlayerSearchUiItem(
        val item: PlayerUiItem,
        val isVideo: Boolean
    )

    fun PlayerState.currentTabItems(): List<PlayerUiItem> {
        return when (selectedTab) {
            PlayerTab.VIDEO -> videos
            PlayerTab.AUDIO -> audios
        }
    }

    fun PlayerState.searchItems(
        query: String,
        filter: PlayerSearchFilter,
    ): List<PlayerSearchUiItem> {
        val normalizedQuery = query.trim()

        val sourceItems = when (filter) {
            PlayerSearchFilter.ALL_TYPES -> {
                videos.map { PlayerSearchUiItem(item = it, isVideo = true) } +
                        audios.map { PlayerSearchUiItem(item = it, isVideo = false) }
            }

            PlayerSearchFilter.VIDEO -> {
                videos.map { PlayerSearchUiItem(item = it, isVideo = true) }
            }

            PlayerSearchFilter.AUDIO -> {
                audios.map { PlayerSearchUiItem(item = it, isVideo = false) }
            }
        }

        if (normalizedQuery.isBlank()) return sourceItems

        return sourceItems.filter { searchItem ->
            searchItem.item.title.contains(
                other = normalizedQuery,
                ignoreCase = true
            ) || searchItem.item.filePath.contains(
                other = normalizedQuery,
                ignoreCase = true
            )
        }
    }

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