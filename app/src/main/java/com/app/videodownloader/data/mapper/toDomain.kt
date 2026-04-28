package com.app.videodownloader.data.mapper

import com.app.videodownloader.data.remote.dto.ReelsResponseDto
import com.app.videodownloader.data.remote.dto.VideoResponseDto
import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.domain.model.ReelCategory
import com.app.videodownloader.domain.model.VideoData
import com.app.videodownloader.domain.model.DownloadOption

fun List<ReelsResponseDto>.toDomain(): List<ReelCategory> {
    return this.map { category ->
        ReelCategory(
            id = category.id,
            name = category.name,
            thumbnail = category.thumbnail,
            reels = category.items.map { item ->
                Reel(
                    id = item.id.toString(),
                    videoUrl = item.url
                )
            }
        )
    }
}




fun VideoResponseDto.toDomain(): VideoData {

    return VideoData(
        status = status,
        title = title.orEmpty(),
        thumbnail = image_url.orEmpty(),
        errorMessage= error_message,
        platform = platform,
        downloadOptions = downloadables.map {
            DownloadOption(
                quality = it.quality,
                url = it.url
            )
        }
    )
}