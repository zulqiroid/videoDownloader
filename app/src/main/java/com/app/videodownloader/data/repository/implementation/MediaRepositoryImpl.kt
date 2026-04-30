package com.app.videodownloader.data.repository.implementation

import android.content.Context
import android.provider.MediaStore
import com.app.videodownloader.domain.repository.MediaRepository
import com.app.videodownloader.presentation.screens.player.states.PlayerUiItem

class MediaRepositoryImpl(
    private val context: Context
) : MediaRepository {

    override suspend fun getVideos(): List<PlayerUiItem> {
        val list = mutableListOf<PlayerUiItem>()

        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA
        )

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {

                val id = it.getLong(0)
                val name = it.getString(1)
                val duration = it.getLong(2)
                val size = it.getLong(3)
                val path = it.getString(4)

                list.add(
                    PlayerUiItem(
                        id = id,
                        title = name,
                        duration = formatDuration(duration),
                        size = formatSize(size),
                        quality = "1080p", // optional logic later
                        filePath = path,
                        date = ""
                    )
                )
            }
        }

        return list
    }

    override suspend fun getAudios(): List<PlayerUiItem> {
        val list = mutableListOf<PlayerUiItem>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA
        )

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {

                val id = it.getLong(0)
                val name = it.getString(1)
                val duration = it.getLong(2)
                val size = it.getLong(3)
                val path = it.getString(4)

                list.add(
                    PlayerUiItem(
                        id = id,
                        title = name,
                        duration = formatDuration(duration),
                        size = formatSize(size),
                        quality = "",
                        filePath = path,
                        date = ""
                    )
                )
            }
        }

        return list
    }

    private fun formatDuration(duration: Long): String {
        val seconds = duration / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }

    private fun formatSize(size: Long): String {
        val mb = size / (1024f * 1024f)
        return String.format("%.2f", mb)
    }
}