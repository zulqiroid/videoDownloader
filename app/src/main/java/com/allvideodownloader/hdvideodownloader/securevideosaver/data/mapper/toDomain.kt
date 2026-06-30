package com.allvideodownloader.hdvideodownloader.securevideosaver.data.mapper

import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.dto.ReelsResponseDto
import com.allvideodownloader.hdvideodownloader.securevideosaver.data.remote.dto.VideoResponseDto
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.Reel
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ReelCategory
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.VideoData
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.DownloadOption

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