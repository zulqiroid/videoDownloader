package com.allvideodownloader.hdvideodownloader.securevideosaver.data.local.dataSource

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object VideoThumbnailUtil {

    suspend fun getThumbnail(videoUrl: String): Bitmap? {

        ThumbnailCache.get(videoUrl)?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(videoUrl, HashMap())

                // ✅ Most efficient way (no manual timestamp)
                val bitmap = retriever.getFrameAtTime(-1)

                retriever.release()

                bitmap?.also {
                    ThumbnailCache.put(videoUrl, it)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}