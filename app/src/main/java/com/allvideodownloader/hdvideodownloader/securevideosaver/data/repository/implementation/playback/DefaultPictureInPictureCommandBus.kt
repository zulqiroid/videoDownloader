package com.allvideodownloader.hdvideodownloader.securevideosaver.data.repository.implementation.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.playback.PictureInPictureCommand
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.PictureInPictureCommandBus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DefaultPictureInPictureCommandBus : PictureInPictureCommandBus {

    private val _commands = MutableSharedFlow<PictureInPictureCommand>(
        replay = 0,
        extraBufferCapacity = COMMAND_BUFFER_CAPACITY
    )

    override val commands: SharedFlow<PictureInPictureCommand> = _commands.asSharedFlow()

    override fun dispatch(command: PictureInPictureCommand) {
        _commands.tryEmit(command)
    }

    private companion object {
        private const val COMMAND_BUFFER_CAPACITY = 8
    }
}