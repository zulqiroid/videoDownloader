package com.app.videodownloader.data.repository.implementation

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.app.videodownloader.domain.repository.MediaRepository
import com.app.videodownloader.presentation.screens.player.states.PlayerUiItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaRepositoryImpl(
    private val context: Context
) : MediaRepository {

    override fun observeVideos(): Flow<List<PlayerUiItem>> {
        return observeMediaStore(
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            query = ::queryVideos
        )
    }

    override fun observeAudios(): Flow<List<PlayerUiItem>> {
        return observeMediaStore(
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            query = ::queryAudios
        )
    }

    private fun observeMediaStore(
        uri: Uri,
        query: suspend () -> List<PlayerUiItem>
    ): Flow<List<PlayerUiItem>> {
        return callbackFlow {
            fun refresh() {
                launch(Dispatchers.IO) {
                    trySend(query())
                }
            }

            val observer = object : ContentObserver(
                Handler(Looper.getMainLooper())
            ) {
                override fun onChange(selfChange: Boolean) {
                    refresh()
                }

                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    refresh()
                }
            }

            context.contentResolver.registerContentObserver(
                uri,
                true,
                observer
            )

            refresh()

            awaitClose {
                context.contentResolver.unregisterContentObserver(observer)
            }
        }
            .conflate()
            .distinctUntilChanged()
    }

    private suspend fun queryVideos(): List<PlayerUiItem> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<PlayerUiItem>()

            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT
            )

            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )?.use { cursor ->

                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val widthIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
                val heightIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val name = cursor.getString(nameIndex).orEmpty()
                    val duration = cursor.getLong(durationIndex)
                    val size = cursor.getLong(sizeIndex)
                    val path = cursor.getString(pathIndex).orEmpty()
                    val width = cursor.getInt(widthIndex)
                    val height = cursor.getInt(heightIndex)

                    list.add(
                        PlayerUiItem(
                            id = id,
                            title = name,
                            duration = formatDuration(duration),
                            size = formatSize(size),
                            quality = buildQualityLabel(width, height),
                            filePath = path,
                            date = ""
                        )
                    )
                }
            }

            list
        }
    }

    private suspend fun queryAudios(): List<PlayerUiItem> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<PlayerUiItem>()

            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATA
            )

            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "${MediaStore.Audio.Media.DATE_ADDED} DESC"
            )?.use { cursor ->

                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val name = cursor.getString(nameIndex).orEmpty()
                    val duration = cursor.getLong(durationIndex)
                    val size = cursor.getLong(sizeIndex)
                    val path = cursor.getString(pathIndex).orEmpty()

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

            list
        }
    }

    private fun buildQualityLabel(
        width: Int,
        height: Int
    ): String {
        return when {
            height >= 2160 -> "4K"
            height >= 1440 -> "1440p"
            height >= 1080 -> "1080p"
            height >= 720 -> "720p"
            height > 0 -> "${height}p"
            width > 0 -> "${width}px"
            else -> ""
        }
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