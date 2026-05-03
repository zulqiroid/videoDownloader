package com.app.videodownloader.presentation.screens.medaPlayer.states

import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.RingtoneTargetType

data class MediaPlayerState(
    val mediaList: List<MediaFile> = emptyList(),
    val currentIndex: Int = 0,

    val isPlaying: Boolean = true,
    val position: Long = 0L,
    val duration: Long = 0L,

    val volume: Float = 1f,
    val isMuted: Boolean = false,

    val playbackSpeed: Float = DEFAULT_PLAYBACK_SPEED,
    val showPlaybackSpeedDialog: Boolean = false,

    val showFileInfoDialog: Boolean = false,
    val fileInfoMediaItem: MediaFile? = null,

    val showRenameFileDialog: Boolean = false,
    val renameMediaItem: MediaFile? = null,
    val renameDraftName: String = "",
    val renameError: String? = null,
    val isRenamingFile: Boolean = false,

    val showDeleteFileDialog: Boolean = false,
    val deleteMediaItem: MediaFile? = null,
    val isDeletingFile: Boolean = false,
    val deleteFileError: String? = null,

    val showSetAsRingtoneDialog: Boolean = false,
    val ringtoneMediaItem: MediaFile? = null,
    val selectedRingtoneTargetType: RingtoneTargetType = RingtoneTargetType.DefaultRingtone,
    val isSettingRingtone: Boolean = false,
    val setRingtoneError: String? = null,

    val isLoading: Boolean = false,

    val showBottomSheet: Boolean = false,
) {
    companion object {
        const val DEFAULT_PLAYBACK_SPEED = 1f
    }
}