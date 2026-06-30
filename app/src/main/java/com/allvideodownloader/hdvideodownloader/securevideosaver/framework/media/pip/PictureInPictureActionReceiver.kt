package com.allvideodownloader.hdvideodownloader.securevideosaver.framework.media.pip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.PictureInPictureCommand
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.PictureInPictureCommandBus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PictureInPictureActionReceiver : BroadcastReceiver(), KoinComponent {

    private val commandBus: PictureInPictureCommandBus by inject()

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {
        if (intent?.action != PictureInPictureActionContract.ACTION_PIP_CONTROL) return

        when (
            intent.getStringExtra(PictureInPictureActionContract.EXTRA_PIP_ACTION)
        ) {
            PictureInPictureActionContract.ACTION_REWIND -> {
                commandBus.dispatch(PictureInPictureCommand.Rewind)
            }

            PictureInPictureActionContract.ACTION_TOGGLE_PLAY_PAUSE -> {
                commandBus.dispatch(PictureInPictureCommand.TogglePlayPause)
            }

            PictureInPictureActionContract.ACTION_FORWARD -> {
                commandBus.dispatch(PictureInPictureCommand.Forward)
            }
        }
    }
}