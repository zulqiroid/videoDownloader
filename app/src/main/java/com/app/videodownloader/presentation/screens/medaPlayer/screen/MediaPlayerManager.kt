package com.app.videodownloader.presentation.screens.medaPlayer.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.app.videodownloader.domain.model.MediaFile
import java.io.File

class MediaPlayerManager(
    context: Context
) {

    val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext)
        .build()
        .apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
        }

    fun play(media: MediaFile) {
        val uri = Uri.fromFile(File(media.filePath))

        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
    }

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun seekTo(position: Long) {
        player.seekTo(position.coerceAtLeast(0L))
    }

    fun forward() {
        val duration = player.duration.takeIf { it > 0 } ?: Long.MAX_VALUE
        val target = (player.currentPosition + 10_000L).coerceAtMost(duration)
        player.seekTo(target)
    }

    fun rewind() {
        val target = (player.currentPosition - 10_000L).coerceAtLeast(0L)
        player.seekTo(target)
    }

    fun setVolume(volume: Float) {
        player.volume = volume.coerceIn(0f, 1f)
    }

    fun release() {
        player.release()
    }
    fun reset() {
        player.stop()
        player.clearMediaItems()
        player.seekTo(0)
    }
}