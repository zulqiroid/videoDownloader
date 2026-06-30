package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.playback

import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.repository.playback.MediaPlaybackStateStore

class ObserveMediaPlaybackStateUseCase(
    private val mediaPlaybackStateStore: MediaPlaybackStateStore
) {
    operator fun invoke() = mediaPlaybackStateStore.state
}