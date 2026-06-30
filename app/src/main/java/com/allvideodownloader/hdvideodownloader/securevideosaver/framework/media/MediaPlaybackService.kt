package com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.AudioEffectsController
import org.koin.android.ext.android.inject

class MediaPlaybackService : MediaSessionService() {

    private val audioEffectsController: AudioEffectsController by inject()

    private var mediaSession: MediaSession? = null
    private var exoPlayer: ExoPlayer? = null

    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            stopServiceIfPlaybackIsFinished(playbackState)
        }

        override fun onMediaItemTransition(
            mediaItem: androidx.media3.common.MediaItem?,
            reason: Int
        ) {
            if (mediaItem == null) return

            /*
             * Notification title Android system ko MediaMetadata se milta hai.
             * Actual mini-player state sync DefaultMediaPlaybackController ke
             * polling observer se hoti hai, taake service domain store directly
             * depend na kare.
             */
        }
        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            audioEffectsController.attachToAudioSession(audioSessionId)
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this)
                .setChannelId(MEDIA_NOTIFICATION_CHANNEL_ID)
                .setChannelName(R.string.media_notification_channel_name)
                .setNotificationId(MEDIA_NOTIFICATION_ID)
                .build()
        )

        val player = ExoPlayer.Builder(applicationContext)
            .build()
            .apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    true
                )

                setHandleAudioBecomingNoisy(true)
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = false
                addListener(playerListener)
            }

        exoPlayer = player

        mediaSession = MediaSession.Builder(
            this,
            player
        )
            .setId(MEDIA_SESSION_ID)
            .build()
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = exoPlayer

        val shouldStopService =
            player == null ||
                    player.mediaItemCount == 0 ||
                    player.playbackState == Player.STATE_ENDED

        if (shouldStopService) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        exoPlayer?.removeListener(playerListener)

        audioEffectsController.detachFromAudioSession()

        mediaSession?.release()
        mediaSession = null

        exoPlayer?.release()
        exoPlayer = null

        super.onDestroy()
    }

    private fun stopServiceIfPlaybackIsFinished(playbackState: Int) {
        val player = exoPlayer ?: return

        val shouldStop =
            playbackState == Player.STATE_ENDED &&
                    player.mediaItemCount == 0

        if (shouldStop) {
            stopSelf()
        }
    }

    private companion object {
        private const val MEDIA_SESSION_ID = "video_downloader_audio_media_session"
        private const val MEDIA_NOTIFICATION_CHANNEL_ID = "audio_playback_channel"
        private const val MEDIA_NOTIFICATION_ID = 3301
    }
}