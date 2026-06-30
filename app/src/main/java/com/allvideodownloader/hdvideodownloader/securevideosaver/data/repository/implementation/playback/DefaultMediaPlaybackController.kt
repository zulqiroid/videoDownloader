package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.PlaybackMediaType
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.MediaPlaybackController
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.MediaPlaybackStateStore
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media.MediaPlaybackMetadataKeys
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media.MediaPlaybackService
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.media3.common.C

class DefaultMediaPlaybackController(
    context: Context,
    private val mediaPlaybackStateStore: MediaPlaybackStateStore,
    private val applicationScope: CoroutineScope,
) : MediaPlaybackController {

    private val appContext = context.applicationContext

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private val pendingCommands = ArrayDeque<(MediaController) -> Unit>()

    private var progressJob: Job? = null
    private var isReleased = false

    init {
        connect()
    }

    override fun togglePlayPause() {
        runWhenReady { controller ->
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }

            publishPlayerSnapshot(controller)
        }
    }

    override fun skipToPrevious() {
        val playbackState = mediaPlaybackStateStore.state.value

        if (playbackState.positionMs > RESTART_PREVIOUS_THRESHOLD_MS) {
            runWhenReady { controller ->
                controller.seekTo(0L)
                publishPlayerSnapshot(controller)
            }
            return
        }

        playSibling(delta = -1)
    }

    override fun skipToNext() {
        playSibling(delta = 1)
    }

    override fun stopAndClear() {
        pendingCommands.clear()

        mediaPlaybackStateStore.clear()

        runWhenReady { controller ->
            controller.pause()
            controller.stop()
            controller.clearMediaItems()
        }
    }

    private fun playSibling(delta: Int) {
        runWhenReady { controller ->
            when {
                delta > 0 && controller.hasNextMediaItem() -> {
                    controller.seekToNextMediaItem()
                    controller.prepare()
                    controller.play()
                }

                delta < 0 && controller.hasPreviousMediaItem() -> {
                    controller.seekToPreviousMediaItem()
                    controller.prepare()
                    controller.play()
                }

                else -> return@runWhenReady
            }

            syncCurrentMediaFromController(controller)
            publishPlayerSnapshot(controller)
        }
    }

    private fun connect() {
        mediaController?.let {
            startProgressObserver()
            return
        }

        if (controllerFuture != null) return

        isReleased = false

        val sessionToken = SessionToken(
            appContext,
            ComponentName(
                appContext,
                MediaPlaybackService::class.java
            )
        )

        val future = MediaController.Builder(
            appContext,
            sessionToken
        ).buildAsync()

        controllerFuture = future

        future.addListener(
            {
                if (isReleased) return@addListener

                runCatching {
                    future.get()
                }.onSuccess { controller ->
                    mediaController = controller
                    drainPendingCommands(controller)
                    startProgressObserver()
                }.onFailure {
                    pendingCommands.clear()
                }
            },
            ContextCompat.getMainExecutor(appContext)
        )
    }

    private fun runWhenReady(command: (MediaController) -> Unit) {
        if (isReleased) return

        val controller = mediaController

        if (controller != null) {
            command(controller)
            startProgressObserver()
        } else {
            pendingCommands.add(command)
            connect()
        }
    }

    private fun drainPendingCommands(controller: MediaController) {
        while (pendingCommands.isNotEmpty()) {
            pendingCommands.removeFirst().invoke(controller)
        }
    }

    private fun startProgressObserver() {
        if (progressJob?.isActive == true) return

        progressJob = applicationScope.launch {
            while (isActive) {
                val playbackState = mediaPlaybackStateStore.state.value
                val controller = mediaController

                if (
                    controller != null &&
                    playbackState.isAudio &&
                    playbackState.hasActiveMedia
                ) {
                    syncCurrentMediaFromController(controller)
                    publishPlayerSnapshot(controller)
                }

                delay(PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun syncCurrentMediaFromController(controller: MediaController) {
        val playbackState = mediaPlaybackStateStore.state.value
        val currentMediaItem = controller.currentMediaItem ?: return

        val currentMedia = currentMediaItem.toMediaFileOrNull() ?: return
        val audioQueue = playbackState.mediaList
            .filterNot { media -> media.isActuallyVideo() }
            .ifEmpty { listOf(currentMedia) }

        val resolvedIndex = audioQueue.indexOfFirst { media ->
            media.id == currentMedia.id || media.filePath == currentMedia.filePath
        }.takeIf { index ->
            index >= 0
        } ?: controller.currentMediaItemIndex.coerceIn(
            minimumValue = 0,
            maximumValue = audioQueue.lastIndex
        )

        val oldMedia = playbackState.currentMedia
        val hasMediaChanged =
            oldMedia == null ||
                    oldMedia.id != currentMedia.id ||
                    oldMedia.filePath != currentMedia.filePath ||
                    playbackState.currentIndex != resolvedIndex

        if (!hasMediaChanged) return

        mediaPlaybackStateStore.setActiveMedia(
            mediaList = audioQueue,
            currentIndex = resolvedIndex,
            mediaType = PlaybackMediaType.Audio,
            isPlaying = controller.isPlaying
        )
    }

    private fun MediaItem.toMediaFileOrNull(): MediaFile? {
        val extras = mediaMetadata.extras ?: return null

        val id = extras.getLong(
            MediaPlaybackMetadataKeys.EXTRA_MEDIA_ID,
            INVALID_MEDIA_ID
        )

        if (id == INVALID_MEDIA_ID) return null

        val fileName = extras.getString(
            MediaPlaybackMetadataKeys.EXTRA_FILE_NAME
        ).orEmpty()

        val filePath = extras.getString(
            MediaPlaybackMetadataKeys.EXTRA_FILE_PATH
        ).orEmpty()

        val contentUri = extras.getString(
            MediaPlaybackMetadataKeys.EXTRA_CONTENT_URI
        ).orEmpty()

        val isVideo = extras.getBoolean(
            MediaPlaybackMetadataKeys.EXTRA_IS_VIDEO,
            false
        )

        if (fileName.isBlank() || filePath.isBlank()) return null

        return MediaFile(
            id = id,
            filePath = filePath,
            fileName = fileName,
            isVideo = isVideo,
            contentUri = contentUri
        )
    }

    private fun publishPlayerSnapshot(player: Player) {
        val duration = player.duration.takeIf { it > 0L } ?: 0L
        val position = player.currentPosition.coerceAtLeast(0L)

        mediaPlaybackStateStore.updatePlayerState(
            isPlaying = player.isPlaying,
            isBuffering = player.playbackState == Player.STATE_BUFFERING,
            positionMs = position,
            durationMs = duration,
            playbackSpeed = player.playbackParameters.speed,
            volume = player.volume,
            isMuted = player.volume == 0f
        )
    }

    private fun MediaFile.toMediaItem(): MediaItem {
        val playableUri = contentUri
            .takeIf { it.isNotBlank() }
            ?.let(Uri::parse)
            ?: Uri.fromFile(File(filePath))

        val extras = Bundle().apply {
            putLong(MediaPlaybackMetadataKeys.EXTRA_MEDIA_ID, id)
            putString(MediaPlaybackMetadataKeys.EXTRA_FILE_NAME, fileName)
            putString(MediaPlaybackMetadataKeys.EXTRA_FILE_PATH, filePath)
            putString(MediaPlaybackMetadataKeys.EXTRA_CONTENT_URI, contentUri)
            putBoolean(MediaPlaybackMetadataKeys.EXTRA_IS_VIDEO, isActuallyVideo())
        }

        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(playableUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(fileName)
                    .setExtras(extras)
                    .build()
            )
            .build()
    }

    private fun MediaFile.matches(other: MediaFile?): Boolean {
        if (other == null) return false

        return id == other.id || filePath == other.filePath
    }

    private fun MediaFile.isActuallyVideo(): Boolean {
        val lowerName = fileName.lowercase()
        val lowerPath = filePath.lowercase()

        return isVideo ||
            lowerName.endsWith(".mp4") ||
            lowerName.endsWith(".mkv") ||
            lowerName.endsWith(".mov") ||
            lowerName.endsWith(".webm") ||
            lowerName.endsWith(".avi") ||
            lowerName.endsWith(".3gp") ||
            lowerName.endsWith(".m4v") ||
            lowerPath.endsWith(".mp4") ||
            lowerPath.endsWith(".mkv") ||
            lowerPath.endsWith(".mov") ||
            lowerPath.endsWith(".webm") ||
            lowerPath.endsWith(".avi") ||
            lowerPath.endsWith(".3gp") ||
            lowerPath.endsWith(".m4v")
    }

    private companion object {
        private const val PROGRESS_UPDATE_INTERVAL_MS = 1_000L
        private const val RESTART_PREVIOUS_THRESHOLD_MS = 3_000L
        private const val INVALID_MEDIA_ID = -1L
    }
}