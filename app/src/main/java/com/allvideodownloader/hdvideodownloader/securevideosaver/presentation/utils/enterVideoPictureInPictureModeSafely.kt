package com.allvideodownloader.hdvideodownloader.securevideosaver.core.utils

import android.app.Activity
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Rational
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media.pip.PictureInPictureActionContract
import com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media.pip.PictureInPictureActionReceiver

fun Activity.enterVideoPictureInPictureModeSafely(
    isPlaying: Boolean,
    title: String,
): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false

    return runCatching {
        enterPictureInPictureMode(
            buildVideoPictureInPictureParams(
                isPlaying = isPlaying,
                title = title
            )
        )
    }.getOrDefault(false)
}

fun Activity.updateVideoPictureInPictureParamsSafely(
    isPlaying: Boolean,
    title: String,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    runCatching {
        setPictureInPictureParams(
            buildVideoPictureInPictureParams(
                isPlaying = isPlaying,
                title = title
            )
        )
    }
}

private fun Activity.buildVideoPictureInPictureParams(
    isPlaying: Boolean,
    title: String,
): PictureInPictureParams {
    return PictureInPictureParams.Builder()
        .setAspectRatio(
            Rational(
                PIP_ASPECT_RATIO_WIDTH,
                PIP_ASPECT_RATIO_HEIGHT
            )
        )
        .setActions(
            buildVideoPictureInPictureActions(
                isPlaying = isPlaying
            )
        )
        .apply {
            /*
             * Very important:
             * Auto PiP OFF hai. Home/Recent press par PiP auto enter nahi hoga.
             * Sirf explicit PiP button click par enter hoga.
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setAutoEnterEnabled(false)
                setSeamlessResizeEnabled(true)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                setTitle(title.ifBlank { "Video" })
                setSubtitle("Video Downloader")
            }
        }
        .build()
}

private fun Activity.buildVideoPictureInPictureActions(
    isPlaying: Boolean,
): List<RemoteAction> {
    return listOf(
        buildPipRemoteAction(
            action = PictureInPictureActionContract.ACTION_REWIND,
            requestCode = REQUEST_CODE_REWIND,
            iconRes = android.R.drawable.ic_media_rew,
            title = "Rewind",
            description = "Rewind 10 seconds"
        ),
        buildPipRemoteAction(
            action = PictureInPictureActionContract.ACTION_TOGGLE_PLAY_PAUSE,
            requestCode = REQUEST_CODE_TOGGLE_PLAY_PAUSE,
            iconRes = if (isPlaying) {
                android.R.drawable.ic_media_pause
            } else {
                android.R.drawable.ic_media_play
            },
            title = if (isPlaying) "Pause" else "Play",
            description = if (isPlaying) "Pause video" else "Play video"
        ),
        buildPipRemoteAction(
            action = PictureInPictureActionContract.ACTION_FORWARD,
            requestCode = REQUEST_CODE_FORWARD,
            iconRes = android.R.drawable.ic_media_ff,
            title = "Forward",
            description = "Forward 10 seconds"
        )
    )
}

private fun Activity.buildPipRemoteAction(
    action: String,
    requestCode: Int,
    iconRes: Int,
    title: String,
    description: String,
): RemoteAction {
    val intent = Intent(
        this,
        PictureInPictureActionReceiver::class.java
    ).apply {
        this.action = PictureInPictureActionContract.ACTION_PIP_CONTROL
        putExtra(
            PictureInPictureActionContract.EXTRA_PIP_ACTION,
            action
        )
    }

    val pendingIntent = PendingIntent.getBroadcast(
        this,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    return RemoteAction(
        Icon.createWithResource(this, iconRes),
        title,
        description,
        pendingIntent
    )
}

private const val PIP_ASPECT_RATIO_WIDTH = 16
private const val PIP_ASPECT_RATIO_HEIGHT = 9

private const val REQUEST_CODE_REWIND = 4101
private const val REQUEST_CODE_TOGGLE_PLAY_PAUSE = 4102
private const val REQUEST_CODE_FORWARD = 4103