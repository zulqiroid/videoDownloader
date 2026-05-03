package com.app.videodownloader.presentation.screens.medaPlayer.screen

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
 import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.presentation.screens.medaPlayer.componants.AudioPlayerItem
import com.app.videodownloader.presentation.screens.medaPlayer.componants.MediaPlayerBottomSheet
import com.app.videodownloader.presentation.screens.medaPlayer.componants.PlaybackSpeedDialog
import com.app.videodownloader.presentation.screens.medaPlayer.componants.VideoPlayerItem
import com.app.videodownloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.app.videodownloader.presentation.screens.medaPlayer.events.MediaPlayerNavEvent
import com.app.videodownloader.presentation.screens.medaPlayer.events.VideoOptionsIntent
import com.app.videodownloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
import com.app.videodownloader.presentation.screens.player.componants.DeleteFileDialog
import com.app.videodownloader.presentation.screens.player.componants.FileInformationDialog
import com.app.videodownloader.presentation.screens.player.componants.RenameFileDialog
import org.koin.compose.viewmodel.koinViewModel
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import com.app.videodownloader.domain.model.ads.BannerAdScreen
import com.app.videodownloader.domain.model.ads.BannerAdSlot
import com.app.videodownloader.presentation.ads.banner.componants.BannerAdHost
import com.app.videodownloader.presentation.ads.banner.viewModel.BannerAdViewModel
import com.app.videodownloader.presentation.screens.medaPlayer.componants.SetAsRingtoneDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaPlayerScreen(
    mediaList: List<MediaFile>,
    startIndex: Int,
    backStack: NavBackStack<NavKey>,
    viewModel: MediaPlayerViewModel = koinViewModel(),
    bannerAdViewModel: BannerAdViewModel = koinViewModel()

) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    val bannerState by bannerAdViewModel.state.collectAsState()

    val bannerScreen = BannerAdScreen.MediaPlayer

    val showTopBanner = bannerState.config.isEnabled(
        screen = bannerScreen,
        slot = BannerAdSlot.Top
    )

    val showBottomBanner = bannerState.config.isEnabled(
        screen = bannerScreen,
        slot = BannerAdSlot.Bottom
    )

    val currentMedia = state.mediaList.getOrNull(state.currentIndex)

    val screenBackgroundColor = if (currentMedia?.isVideo == true) {
        Color.Black
    } else {
        Color.White
    }

    val writePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onBottomSheetIntent(
                VideoOptionsIntent.OnRenamePermissionGranted
            )
        } else {
            viewModel.onBottomSheetIntent(
                VideoOptionsIntent.OnRenamePermissionDenied
            )
        }
    }

    val deletePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onBottomSheetIntent(
                VideoOptionsIntent.OnDeletePermissionGranted
            )
        } else {
            viewModel.onBottomSheetIntent(
                VideoOptionsIntent.OnDeletePermissionDenied
            )
        }
    }

    val writeSettingsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.onBottomSheetIntent(
            VideoOptionsIntent.OnWriteSettingsPermissionReturned
        )
    }

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                is MediaPlayerNavEvent.RequestMediaWritePermission -> {
                    val currentActivity = activity ?: return@collect

                    val intentSender = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MediaStore.createWriteRequest(
                            currentActivity.contentResolver,
                            listOf(event.uri)
                        ).intentSender
                    } else {
                        event.pendingIntent?.intentSender
                    }

                    if (intentSender != null) {
                        writePermissionLauncher.launch(
                            IntentSenderRequest.Builder(intentSender).build()
                        )
                    } else {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnRenamePermissionDenied
                        )
                    }
                }

                is MediaPlayerNavEvent.RequestMediaDeletePermission -> {
                    val currentActivity = activity ?: return@collect

                    val intentSender = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MediaStore.createDeleteRequest(
                            currentActivity.contentResolver,
                            listOf(event.uri)
                        ).intentSender
                    } else {
                        event.pendingIntent?.intentSender
                    }

                    if (intentSender != null) {
                        deletePermissionLauncher.launch(
                            IntentSenderRequest.Builder(intentSender).build()
                        )
                    } else {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnDeletePermissionDenied
                        )
                    }
                }

                is MediaPlayerNavEvent.ShareMediaFile -> {
                    activity?.shareMediaFile(event.mediaFile)
                }

                MediaPlayerNavEvent.CloseMediaPlayer -> {
                    backStack.removeLastOrNull()
                }

                MediaPlayerNavEvent.RequestWriteSettingsPermission -> {
                    val currentActivity = activity ?: return@collect

                    val intent = Intent(
                        Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:${currentActivity.packageName}")
                    )

                    writeSettingsPermissionLauncher.launch(intent)
                }
            }
        }
    }

    LaunchedEffect(mediaList, startIndex) {
        viewModel.onEvent(
            MediaPlayerEvent.Load(
                mediaList = mediaList,
                startIndex = startIndex
            )
        )
    }

    if (state.mediaList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
        return
    }

    val pagerState = rememberPagerState(
        initialPage = state.currentIndex,
        pageCount = { state.mediaList.size }
    )

    LaunchedEffect(state.currentIndex) {
        if (pagerState.currentPage != state.currentIndex) {
            pagerState.animateScrollToPage(state.currentIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(
            MediaPlayerEvent.OnPageChanged(pagerState.currentPage)
        )
    }

    BackHandler {
        viewModel.onEvent(MediaPlayerEvent.OnBackPressed)
        backStack.removeLastOrNull()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = screenBackgroundColor,
        topBar = {
            Column(
                modifier = Modifier.padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showTopBanner){
                    BannerAdHost(
                        config = bannerState.config,
                        screen = bannerScreen,
                        slot = BannerAdSlot.Top
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.padding(
                    top = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showBottomBanner){
                    BannerAdHost(
                        config = bannerState.config,
                        screen = bannerScreen,
                        slot = BannerAdSlot.Bottom
                    )
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->

                val media = state.mediaList[page]
                val isCurrentPage = state.currentIndex == page

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    if (isCurrentPage) {
                        if (media.isVideo) {
                            VideoPlayerItem(
                                media = media,
                                viewModel = viewModel,
                                onBack = {
                                    viewModel.onEvent(MediaPlayerEvent.OnBackPressed)
                                    backStack.removeLastOrNull()
                                },
                                onThreeDotsClick = {
                                    viewModel.onEvent(MediaPlayerEvent.OnThreeDotsClick)
                                }
                            )
                        } else {
                            AudioPlayerItem(
                                media = media,
                                viewModel = viewModel,
                                onBack = {
                                    viewModel.onEvent(MediaPlayerEvent.OnBackPressed)
                                    backStack.removeLastOrNull()
                                },
                                onPlayPause = {
                                    viewModel.onEvent(MediaPlayerEvent.OnPlayPauseClicked)
                                },
                                onForward = {
                                    viewModel.onEvent(MediaPlayerEvent.OnForwardClicked)
                                },
                                onRewind = {
                                    viewModel.onEvent(MediaPlayerEvent.OnRewindClicked)
                                },
                                onNext = {
                                    viewModel.onEvent(MediaPlayerEvent.OnNextClicked)
                                },
                                onPrevious = {
                                    viewModel.onEvent(MediaPlayerEvent.OnPreviousClicked)
                                },
                                onSeek = {
                                    viewModel.onEvent(MediaPlayerEvent.OnSeek(it))
                                },
                                onMuteToggle = {
                                    viewModel.onEvent(MediaPlayerEvent.OnMuteToggleClicked)
                                },
                                onVolumeChange = {
                                    viewModel.onEvent(MediaPlayerEvent.OnVolumeChanged(it))
                                },
                                onThreeDotsClick = {
                                    viewModel.onEvent(MediaPlayerEvent.OnThreeDotsClick)
                                }
                            )
                        }
                    }
                }
            }

            val currentMedia = state.mediaList.getOrNull(state.currentIndex)

            if (state.showBottomSheet && currentMedia != null) {
                MediaPlayerBottomSheet(
                    state = currentMedia,
                    playbackSpeed = state.playbackSpeed,
                    onIntent = viewModel::onBottomSheetIntent
                )
            }

            if (state.showPlaybackSpeedDialog) {
                PlaybackSpeedDialog(
                    selectedSpeed = state.playbackSpeed,
                    onSpeedSelected = { speed ->
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnPlaybackSpeedSelected(speed)
                        )
                    },
                    onResetClicked = {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnPlaybackSpeedResetClicked
                        )
                    },
                    onDismiss = {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnPlaybackSpeedDialogDismissed
                        )
                    }
                )
            }

            if (state.showFileInfoDialog) {
                FileInformationDialog(
                    item = state.fileInfoMediaItem,
                    onDismiss = {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnFileInfoDismissed
                        )
                    }
                )
            }

            if (state.showRenameFileDialog) {
                RenameFileDialog(
                    fileName = state.renameDraftName,
                    errorMessage = state.renameError,
                    isLoading = state.isRenamingFile,
                    onValueChange = { value ->
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnRenameValueChanged(value)
                        )
                    },
                    onDismiss = {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnRenameDismissed
                        )
                    },
                    onConfirmClick = {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnRenameConfirmClicked
                        )
                    }
                )
            }

            if (state.showDeleteFileDialog) {
                DeleteFileDialog(
                    isLoading = state.isDeletingFile,
                    errorMessage = state.deleteFileError,
                    onCancelClick = {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnDeleteDismissed
                        )
                    },
                    onDeleteClick = {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnDeleteConfirmClicked
                        )
                    }
                )
            }

            if (state.showSetAsRingtoneDialog) {
                SetAsRingtoneDialog(
                    selectedType = state.selectedRingtoneTargetType,
                    isLoading = state.isSettingRingtone,
                    errorMessage = state.setRingtoneError,
                    onTypeSelected = { type ->
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnRingtoneTargetSelected(type)
                        )
                    },
                    onDismiss = {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnSetAsRingtoneDismissed
                        )
                    },
                    onConfirmClick = {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnSetAsRingtoneConfirmClicked
                        )
                    }
                )
            }
        }
    }
}

private fun Activity.shareMediaFile(
    mediaFile: MediaFile
) {
    val uri = mediaFile.toMediaStoreUri()

    val mimeType = if (mediaFile.isVideo) {
        "video/*"
    } else {
        "audio/*"
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(
        shareIntent,
        "Share ${mediaFile.fileName}"
    )

    if (shareIntent.resolveActivity(packageManager) != null) {
        startActivity(chooser)
    }
}

private fun MediaFile.toMediaStoreUri(): android.net.Uri {
    val collectionUri = if (isVideo) {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    return ContentUris.withAppendedId(collectionUri, id)
}