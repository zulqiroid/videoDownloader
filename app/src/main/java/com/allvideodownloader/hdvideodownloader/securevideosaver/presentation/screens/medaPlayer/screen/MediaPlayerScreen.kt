package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.screen

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.allvideodownloader.hdvideodownloader.securevideosaver.core.utils.enterVideoPictureInPictureModeSafely
import com.allvideodownloader.hdvideodownloader.securevideosaver.core.utils.updateVideoPictureInPictureParamsSafely
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.BannerAdScreen
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.BannerAdSlot
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPosition
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.banner.componants.BannerAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.banner.viewModel.BannerAdViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants.AudioPlayerItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants.MediaPlayerBottomSheet
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants.PlaybackSpeedDialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants.SetAsRingtoneDialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants.VideoPlayerItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.events.MediaPlayerNavEvent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.events.VideoOptionsIntent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.states.MediaPlayerState
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.componants.DeleteFileDialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.componants.FileInformationDialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.componants.RenameFileDialog
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.medaPlayer.componants.AudioEffectsBottomSheet

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaPlayerScreen(
    mediaList: List<MediaFile>,
    startIndex: Int,
    backStack: NavBackStack<NavKey>,
    viewModel: MediaPlayerViewModel = koinViewModel(),
    bannerAdViewModel: BannerAdViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val bannerState by bannerAdViewModel.state.collectAsStateWithLifecycle()

    val activity = LocalActivity.current
    val configuration = LocalConfiguration.current

    var isLandscapeMode by remember {
        mutableStateOf(false)
    }

    val isDeviceLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isLandscape = isLandscapeMode || isDeviceLandscape

    fun enterLandscape() {
        isLandscapeMode = true
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun enterPortrait() {
        isLandscapeMode = false
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun exitPlayer() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        viewModel.onEvent(MediaPlayerEvent.OnBackPressed)
        backStack.removeLastOrNull()
    }

    val screenBackgroundColor = Color(state.screenBackgroundColor)

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

                MediaPlayerNavEvent.RequestPictureInPicture -> {
                    val currentMedia = state.mediaList.getOrNull(state.currentIndex)
                    activity?.enterVideoPictureInPictureModeSafely(
                        isPlaying = state.isPlaying,
                        title = currentMedia?.fileName.orEmpty()
                    )
                }
            }
        }
    }

    LaunchedEffect(
        state.isInPictureInPictureMode,
        state.isPlaying,
        state.currentIndex,
        state.mediaList
    ) {
        if (!state.isInPictureInPictureMode) return@LaunchedEffect

        val currentMedia = state.mediaList.getOrNull(state.currentIndex)

        activity?.updateVideoPictureInPictureParamsSafely(
            isPlaying = state.isPlaying,
            title = currentMedia?.fileName.orEmpty()
        )
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

    val initialPagerPage = remember(
        state.mediaList,
        startIndex
    ) {
        startIndex.coerceIn(
            minimumValue = 0,
            maximumValue = (state.mediaList.size - 1).coerceAtLeast(0)
        )
    }

    val pagerState = rememberPagerState(
        initialPage = initialPagerPage,
        pageCount = {
            state.mediaList.size
        }
    )

    val canShowAds = !state.isPremiumUser
    val bannerScreen = BannerAdScreen.MediaPlayer

    val shouldShowMediaPlayerBanner =
        canShowAds && !isLandscape

    val showTopBanner =
        shouldShowMediaPlayerBanner &&
                bannerState.config.isEnabled(
                    screen = bannerScreen,
                    slot = BannerAdSlot.Top
                )

    val showBottomBanner =
        shouldShowMediaPlayerBanner &&
                bannerState.config.isEnabled(
                    screen = bannerScreen,
                    slot = BannerAdSlot.Bottom
                )

    val placementKey = NativeAdConfig.MEDIA_PLAYER


    val placementConfig = if (canShowAds) {
        state.nativeAdConfig.placement(placementKey)
    } else {
        null
    }

    val nativeAd = if (canShowAds) {
        state.nativeAds[placementKey]
    } else {
        null
    }


    val showTopNativeAd =
        placementConfig?.position == NativeAdPosition.Top &&
                !state.isPremiumUser &&
                nativeAd != null

    val showBottomNativeAd =
        placementConfig?.position == NativeAdPosition.Bottom &&
                !state.isPremiumUser &&
                nativeAd != null


    LaunchedEffect(state.currentIndex, state.mediaList.size) {
        val targetPage = state.currentIndex.coerceIn(
            minimumValue = 0,
            maximumValue = (state.mediaList.size - 1).coerceAtLeast(0)
        )

        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState, state.mediaList.size) {
        snapshotFlow {
            pagerState.currentPage
        }
            .distinctUntilChanged()
            .collect { page ->
                val safePage = page.coerceIn(
                    minimumValue = 0,
                    maximumValue = (state.mediaList.size - 1).coerceAtLeast(0)
                )

                viewModel.onEvent(
                    MediaPlayerEvent.OnPageChanged(safePage)
                )
            }
    }

    if (state.isInPictureInPictureMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            MediaPlayerContent(
                state = state,
                pagerState = pagerState,
                screenBackgroundColor = Color.Black,
                isLandscape = true,
                viewModel = viewModel,
                enterPortrait = ::enterPortrait,
                enterLandscape = ::enterLandscape,
                exitPlayer = ::exitPlayer
            )
        }
        return
    }

    BackHandler {
        if (isLandscape) {
            enterPortrait()
        } else {
            exitPlayer()
        }
    }

    if (isLandscape) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackgroundColor)
        ) {
            MediaPlayerContent(
                state = state,
                pagerState = pagerState,
                screenBackgroundColor = screenBackgroundColor,
                isLandscape = true,
                viewModel = viewModel,
                enterPortrait = ::enterPortrait,
                enterLandscape = ::enterLandscape,
                exitPlayer = ::exitPlayer
            )
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = screenBackgroundColor,
            contentWindowInsets = WindowInsets(0.dp),
            topBar = {
                Column(
                    modifier = Modifier
                        .background(screenBackgroundColor)
                        .padding(
                            top = WindowInsets.statusBars
                                .asPaddingValues()
                                .calculateTopPadding()
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showTopBanner) {
                        BannerAdHost(
                            config = bannerState.config,
                            screen = bannerScreen,
                            slot = BannerAdSlot.Top
                        )
                    }

                    if (showTopNativeAd) {
                        NativeAdHost(
                            nativeAd = nativeAd,
                            nativeAdConfig = state.nativeAdConfig,
                            placementConfig = placementConfig,
                            placementKey = placementKey
                        )
                    }
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .background(screenBackgroundColor)
                        .padding(
                            bottom = WindowInsets.navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding()
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showBottomNativeAd) {
                        NativeAdHost(
                            nativeAd = nativeAd,
                            nativeAdConfig = state.nativeAdConfig,
                            placementConfig = placementConfig,
                            placementKey = placementKey
                        )
                    }

                    if (showBottomBanner) {
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
                    .padding(paddingValues)
            ) {
                MediaPlayerContent(
                    state = state,
                    pagerState = pagerState,
                    screenBackgroundColor = screenBackgroundColor,
                    isLandscape = false,
                    viewModel = viewModel,
                    enterPortrait = ::enterPortrait,
                    enterLandscape = ::enterLandscape,
                    exitPlayer = ::exitPlayer
                )
            }
        }
    }
}

private fun Activity.shareMediaFile(
    mediaFile: MediaFile,
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

private fun MediaFile.toMediaStoreUri(): Uri {
    val collectionUri = if (isVideo) {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    return ContentUris.withAppendedId(collectionUri, id)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaPlayerContent(
    state: MediaPlayerState,
    pagerState: androidx.compose.foundation.pager.PagerState,
    screenBackgroundColor: Color,
    isLandscape: Boolean,
    viewModel: MediaPlayerViewModel,
    enterPortrait: () -> Unit,
    enterLandscape: () -> Unit,
    exitPlayer: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 0
        ) { page ->

            val media = state.mediaList.getOrNull(page)

            if (media != null) {
                val isCurrentPage = state.currentIndex == page

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(screenBackgroundColor)
                ) {
                    if (isCurrentPage) {
                        if (media.isVideo) {
                            VideoPlayerItem(
                                media = media,
                                viewModel = viewModel,
                                isLandscape = isLandscape,
                                onBack = {
                                    if (isLandscape) {
                                        enterPortrait()
                                    } else {
                                        exitPlayer()
                                    }
                                },
                                onRotateClick = {
                                    if (isLandscape) {
                                        enterPortrait()
                                    } else {
                                        enterLandscape()
                                    }
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
                                    exitPlayer()
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
                                },
                                onShuffleClick = {
                                    viewModel.onEvent(MediaPlayerEvent.OnShuffleClicked)
                                },
                                onRepeatClick = {
                                    viewModel.onEvent(MediaPlayerEvent.OnAudioRepeatClicked)
                                }
                            )
                        }
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

        if (state.showAudioEffectsSheet) {
            AudioEffectsBottomSheet(
                state = state.audioEffectsState,
                onIntent = viewModel::onBottomSheetIntent
            )
        }
    }
}