package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.screen

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.PlaybackMediaType
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.AudioEffectsController
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.MediaPlaybackStateStore
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media.FloatingVideoPlayerCoordinator
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media.MediaPlaybackMetadataKeys
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media.MediaPlaybackService
import com.google.common.util.concurrent.ListenableFuture
import java.io.File

class MediaPlayerManager(
    context: Context,
    private val mediaPlaybackStateStore: MediaPlaybackStateStore,
    private val floatingVideoPlayerCoordinator: FloatingVideoPlayerCoordinator,
    private val audioEffectsController: AudioEffectsController,
){

    private val appContext = context.applicationContext

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var videoPlayer: Player? = null

    private val pendingAudioCommands = ArrayDeque<(MediaController) -> Unit>()

    private val videoAudioSessionListener = object : Player.Listener {

        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            attachAudioEffectsToVideoSession(audioSessionId)
        }
    }

    private var activePlayerType: ActivePlayerType = ActivePlayerType.None
    private var isReleased = false

    private var activeQueue: List<MediaFile> = emptyList()
    private var activeQueueIndex: Int = 0

    val player: Player?
        get() = when (activePlayerType) {
            ActivePlayerType.Audio -> mediaController
            ActivePlayerType.Video -> videoPlayer
            ActivePlayerType.None -> mediaController ?: videoPlayer
        }

    fun connect(
        onConnected: (Player) -> Unit = {},
        onConnectionFailed: (Throwable) -> Unit = {}
    ) {
        mediaController?.let { controller ->
            onConnected(controller)
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
                    drainPendingAudioCommands(controller)
                    onConnected(controller)
                }.onFailure { throwable ->
                    pendingAudioCommands.clear()
                    onConnectionFailed(throwable)
                }
            },
            ContextCompat.getMainExecutor(appContext)
        )
    }

    fun setQueueSnapshot(
        mediaList: List<MediaFile>,
        currentIndex: Int
    ) {
        activeQueue = mediaList

        activeQueueIndex = if (mediaList.isEmpty()) {
            0
        } else {
            currentIndex.coerceIn(
                minimumValue = 0,
                maximumValue = mediaList.lastIndex
            )
        }
    }

    fun play(media: MediaFile) {
        if (activeQueue.isEmpty()) {
            setQueueSnapshot(
                mediaList = listOf(media),
                currentIndex = 0
            )
        }

        if (media.isActuallyVideo()) {
            playVideo(media)
        } else {
            playAudio(media)
        }
    }

    fun play() {
        runOnActivePlayer { activePlayer ->
            activePlayer.play()
            publishPlayerSnapshot(activePlayer)
        }
    }

    fun pause() {
        runOnActivePlayer { activePlayer ->
            activePlayer.pause()
            publishPlayerSnapshot(activePlayer)
        }
    }

    fun seekTo(position: Long) {
        runOnActivePlayer { activePlayer ->
            activePlayer.seekTo(position.coerceAtLeast(0L))
            publishPlayerSnapshot(activePlayer)
        }
    }

    fun forward() {
        runOnActivePlayer { activePlayer ->
            val duration = activePlayer.duration.takeIf { it > 0L } ?: Long.MAX_VALUE
            val targetPosition = (activePlayer.currentPosition + SEEK_STEP_MS).coerceAtMost(duration)

            activePlayer.seekTo(targetPosition)
            publishPlayerSnapshot(activePlayer)
        }
    }

    fun rewind() {
        runOnActivePlayer { activePlayer ->
            val targetPosition = (activePlayer.currentPosition - SEEK_STEP_MS).coerceAtLeast(0L)

            activePlayer.seekTo(targetPosition)
            publishPlayerSnapshot(activePlayer)
        }
    }

    fun setVolume(volume: Float) {
        val safeVolume = volume.coerceIn(0f, 1f)

        runOnActivePlayer { activePlayer ->
            activePlayer.volume = safeVolume
            publishPlayerSnapshot(activePlayer)
        }

        mediaPlaybackStateStore.updateVolume(
            volume = safeVolume,
            isMuted = safeVolume == 0f
        )
    }

    fun setPlaybackSpeed(speed: Float) {
        val safeSpeed = speed.coerceIn(
            minimumValue = MIN_PLAYBACK_SPEED,
            maximumValue = MAX_PLAYBACK_SPEED
        )

        runOnActivePlayer { activePlayer ->
            activePlayer.playbackParameters = PlaybackParameters(safeSpeed)
            publishPlayerSnapshot(activePlayer)
        }

        mediaPlaybackStateStore.updatePlaybackSpeed(safeSpeed)
    }

    fun publishProgressSnapshot() {
        val activePlayer = when (activePlayerType) {
            ActivePlayerType.Audio -> mediaController
            ActivePlayerType.Video -> videoPlayer
            ActivePlayerType.None -> mediaController ?: videoPlayer
        }

        activePlayer?.let(::publishPlayerSnapshot)
    }

    fun reset() {
        pendingAudioCommands.clear()

        runCatching {
            mediaController?.stop()
            mediaController?.clearMediaItems()
        }

        runCatching {
            detachVideoAudioEffectsListener(videoPlayer)
            videoPlayer?.stop()
            videoPlayer?.clearMediaItems()
        }

        audioEffectsController.detachFromAudioSession()

        activePlayerType = ActivePlayerType.None
        activeQueue = emptyList()
        activeQueueIndex = 0

        mediaPlaybackStateStore.clear()
    }

    fun release() {
        isReleased = true
        pendingAudioCommands.clear()

        runCatching {
            mediaController?.stop()
            mediaController?.clearMediaItems()
            mediaController?.release()
        }

        mediaController = null

        runCatching {
            controllerFuture?.cancel(true)
        }

        controllerFuture = null

        val activeVideoPlayer = videoPlayer

        detachVideoAudioEffectsListener(activeVideoPlayer)

        runCatching {
            activeVideoPlayer?.release()
        }

        videoPlayer = null

        audioEffectsController.detachFromAudioSession()
        activePlayerType = ActivePlayerType.None
        activeQueue = emptyList()
        activeQueueIndex = 0

        mediaPlaybackStateStore.clear()
    }

    fun detachFromScreenKeepingAudioAlive() {
        if (activePlayerType != ActivePlayerType.Audio) {
            release()
            return
        }

        /*
         * IMPORTANT:
         * Audio service ko alive rakhna hai, lekin manager ko permanently released
         * mark nahi karna. Navigation/Koin kabhi kabhi same ViewModel instance reuse
         * kar leta hai. Agar isReleased = true reh gaya to next audio play command
         * ignore ho jati hai.
         */
        isReleased = false
        pendingAudioCommands.clear()

        runCatching {
            mediaController?.release()
        }

        mediaController = null

        runCatching {
            controllerFuture?.cancel(true)
        }

        controllerFuture = null

        runCatching {
            videoPlayer?.release()
        }

        videoPlayer = null

        /*
         * activePlayerType = Audio intentionally keep kar rahe hain.
         * Is se global playback state mini-player ko continue show karta rahega.
         */
    }

    fun detachVideoFromScreenKeepingMiniPlayerAlive() {
        if (activePlayerType != ActivePlayerType.Video) {
            release()
            return
        }

        val activeVideoPlayer = videoPlayer

        if (activeVideoPlayer == null) {
            release()
            return
        }

        floatingVideoPlayerCoordinator.attach(
            player = activeVideoPlayer,
            mediaList = activeQueue,
            currentIndex = activeQueueIndex
        )

        detachVideoAudioEffectsListener(activeVideoPlayer)

        videoPlayer = null
        activePlayerType = ActivePlayerType.None
    }

    private fun buildAudioQueueForNotification(
        selectedMedia: MediaFile
    ): Pair<List<MediaFile>, Int> {
        val audioQueue = activeQueue
            .filterNot { media -> media.isActuallyVideo() }
            .ifEmpty { listOf(selectedMedia) }

        val selectedIndex = audioQueue.indexOfFirst { media ->
            media.id == selectedMedia.id || media.filePath == selectedMedia.filePath
        }.takeIf { index ->
            index >= 0
        } ?: 0

        return audioQueue to selectedIndex
    }

    private fun playAudio(media: MediaFile) {
        activePlayerType = ActivePlayerType.Audio

        val (audioQueue, audioIndex) = buildAudioQueueForNotification(media)

        activeQueue = audioQueue
        activeQueueIndex = audioIndex

        releaseVideoPlayer()

        mediaPlaybackStateStore.setActiveMedia(
            mediaList = audioQueue,
            currentIndex = audioIndex,
            mediaType = PlaybackMediaType.Audio,
            isPlaying = true
        )

        runWhenAudioControllerReady { controller ->
            val mediaItems = audioQueue.map { item ->
                item.toMediaItem()
            }

            controller.repeatMode = Player.REPEAT_MODE_OFF
            controller.setMediaItems(
                mediaItems,
                audioIndex,
                C.TIME_UNSET
            )
            controller.prepare()
            controller.play()

            publishPlayerSnapshot(controller)
        }
    }

    private fun playVideo(media: MediaFile) {
        activePlayerType = ActivePlayerType.Video
        activeQueueIndex = resolveQueueIndex(media)
        pendingAudioCommands.clear()

        clearAudioServicePlaylist()

        publishActiveMedia(
            media = media,
            mediaType = PlaybackMediaType.Video,
            isPlaying = true
        )

        val restoredFloatingPlayer =
            floatingVideoPlayerCoordinator.consumePlayerForFullPlayer(media)

        val player = restoredFloatingPlayer ?: getOrCreateVideoPlayer()

        videoPlayer = player

        attachVideoAudioEffectsListener(player)

        if (restoredFloatingPlayer == null) {
            player.repeatMode = Player.REPEAT_MODE_ONE
            player.setMediaItem(media.toMediaItem())
            player.prepare()
            player.play()
        } else {
            player.repeatMode = Player.REPEAT_MODE_ONE

            if (player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }

            if (!player.isPlaying) {
                player.play()
            }
        }

        publishPlayerSnapshot(player)
    }

    private fun runOnActivePlayer(command: (Player) -> Unit) {
        if (isReleased) return

        when (activePlayerType) {
            ActivePlayerType.Audio -> {
                runWhenAudioControllerReady(command)
            }

            ActivePlayerType.Video -> {
                videoPlayer?.let(command)
            }

            ActivePlayerType.None -> {
                player?.let(command)
            }
        }
    }

    private fun runWhenAudioControllerReady(command: (MediaController) -> Unit) {
        if (isReleased) return

        val controller = mediaController

        if (controller != null) {
            command(controller)
        } else {
            pendingAudioCommands.add(command)
            connect()
        }
    }

    private fun drainPendingAudioCommands(controller: MediaController) {
        while (pendingAudioCommands.isNotEmpty()) {
            pendingAudioCommands.removeFirst().invoke(controller)
        }
    }

    private fun attachVideoAudioEffectsListener(
        player: Player
    ) {
        player.removeListener(videoAudioSessionListener)
        player.addListener(videoAudioSessionListener)

        if (player is ExoPlayer && player.audioSessionId > 0) {
            attachAudioEffectsToVideoSession(player.audioSessionId)
        }
    }

    private fun detachVideoAudioEffectsListener(
        player: Player?
    ) {
        runCatching {
            player?.removeListener(videoAudioSessionListener)
        }
    }

    private fun attachAudioEffectsToVideoSession(
        audioSessionId: Int
    ) {
        if (audioSessionId <= 0) return

        audioEffectsController.attachToAudioSession(audioSessionId)
    }

    private fun getOrCreateVideoPlayer(): Player {
        return videoPlayer ?: ExoPlayer.Builder(appContext)
            .build()
            .apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build(),
                    true
                )

                setHandleAudioBecomingNoisy(true)
                playWhenReady = true

                addListener(videoAudioSessionListener)

                if (audioSessionId > 0) {
                    attachAudioEffectsToVideoSession(audioSessionId)
                }
            }
            .also { player ->
                videoPlayer = player
            }
    }

    private fun clearAudioServicePlaylist() {
        runCatching {
            mediaController?.stop()
            mediaController?.clearMediaItems()
        }
    }

    private fun releaseVideoPlayer() {
        val activeVideoPlayer = videoPlayer

        detachVideoAudioEffectsListener(activeVideoPlayer)

        runCatching {
            activeVideoPlayer?.stop()
            activeVideoPlayer?.clearMediaItems()
            activeVideoPlayer?.release()
        }

        videoPlayer = null

        audioEffectsController.detachFromAudioSession()
    }

    private fun publishActiveMedia(
        media: MediaFile,
        mediaType: PlaybackMediaType,
        isPlaying: Boolean
    ) {
        val queue = activeQueue.takeIf { it.isNotEmpty() } ?: listOf(media)
        val safeIndex = activeQueueIndex.coerceIn(
            minimumValue = 0,
            maximumValue = queue.lastIndex
        )

        mediaPlaybackStateStore.setActiveMedia(
            mediaList = queue,
            currentIndex = safeIndex,
            mediaType = mediaType,
            isPlaying = isPlaying
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

    private fun resolveQueueIndex(media: MediaFile): Int {
        if (activeQueue.isEmpty()) return 0

        val exactIndex = activeQueue.indexOfFirst { item ->
            item.id == media.id && item.filePath == media.filePath
        }

        if (exactIndex >= 0) return exactIndex

        val pathIndex = activeQueue.indexOfFirst { item ->
            item.filePath == media.filePath
        }

        if (pathIndex >= 0) return pathIndex

        return activeQueueIndex.coerceIn(
            minimumValue = 0,
            maximumValue = activeQueue.lastIndex
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

    private enum class ActivePlayerType {
        None,
        Audio,
        Video
    }

    private companion object {
        private const val SEEK_STEP_MS = 10_000L
        private const val MIN_PLAYBACK_SPEED = 0.25f
        private const val MAX_PLAYBACK_SPEED = 4f
    }
}