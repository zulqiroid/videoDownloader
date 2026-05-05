package com.app.videodownloader.presentation.screens.main.screen

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.IntentSenderRequest.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.FromWhichSrc
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.ads.BannerAdScreen
import com.app.videodownloader.domain.model.ads.BannerAdSlot
import com.app.videodownloader.presentation.ads.banner.componants.BannerAdHost
import com.app.videodownloader.presentation.ads.banner.viewModel.BannerAdViewModel
import com.app.videodownloader.presentation.componants.DownloadStartedDialog
import com.app.videodownloader.presentation.componants.exitConfirmationDialogue.ExitConfirmationDialog
import com.app.videodownloader.presentation.componants.privacyPolicyDialgue.PrivacyDialogHost
import com.app.videodownloader.presentation.navigation.Screen
import com.app.videodownloader.presentation.navigation.Screen.*
import com.app.videodownloader.presentation.screens.download.screen.DownloadScreen
import com.app.videodownloader.presentation.screens.home.screen.HomeScreen
import com.app.videodownloader.presentation.screens.main.componants.DownloadBottomSheet
import com.app.videodownloader.presentation.screens.main.componants.FetchingDialog
import com.app.videodownloader.presentation.screens.main.componants.MainBottomBar
import com.app.videodownloader.presentation.screens.main.componants.MediaPermissionRequiredDialog
import com.app.videodownloader.presentation.screens.main.componants.MovingFileDialog
import com.app.videodownloader.presentation.screens.main.componants.NotificationSettingsDialog
import com.app.videodownloader.presentation.screens.main.componants.RateUsDialog
import com.app.videodownloader.presentation.screens.main.componants.SendFeedbackDialog
import com.app.videodownloader.presentation.screens.main.componants.TopBar
import com.app.videodownloader.presentation.screens.main.componants.VideoFetchFailedDialog
import com.app.videodownloader.presentation.screens.main.events.MainEvents
import com.app.videodownloader.presentation.screens.main.events.MainEvents.*
import com.app.videodownloader.presentation.screens.main.events.MainNavEvents
import com.app.videodownloader.presentation.screens.main.states.BottomNavItem
import com.app.videodownloader.presentation.screens.main.states.DownloadSheetState
import com.app.videodownloader.presentation.screens.main.viewModel.MainViewModel
import com.app.videodownloader.presentation.screens.more.screen.MoreScreen
import com.app.videodownloader.presentation.screens.player.componants.FileOptionsDialog
import com.app.videodownloader.presentation.screens.main.events.FileDialogIntent
import com.app.videodownloader.presentation.screens.main.events.NotificationEvents
import com.app.videodownloader.presentation.screens.player.componants.DeleteFileDialog
import com.app.videodownloader.presentation.screens.player.componants.FileInformationDialog
import com.app.videodownloader.presentation.screens.player.componants.RenameFileDialog
import com.app.videodownloader.presentation.screens.player.screen.PlayerScreen
import com.app.videodownloader.presentation.screens.player.states.PlayerUiItem
import com.app.videodownloader.presentation.screens.reels.screen.ReelsScreen
import com.app.videodownloader.presentation.screens.social.screen.Social
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: MainViewModel = koinViewModel(),
    bannerAdViewModel: BannerAdViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val bannerState by bannerAdViewModel.state.collectAsStateWithLifecycle()


    val currentBannerScreen = state.selectedTab.toBannerAdScreen()

    val canShowAds = !state.isPremiumUser

    val showTopBanner = canShowAds && bannerState.config.isEnabled(
        screen = currentBannerScreen,
        slot = BannerAdSlot.Top
    )

    val showBottomBanner = canShowAds && bannerState.config.isEnabled(
        screen = currentBannerScreen,
        slot = BannerAdSlot.Bottom
    )

    val bottomBannerHeight = if (showBottomBanner) 70.dp else 0.dp

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val activity = LocalActivity.current
    val context = LocalContext.current
    val mediaPermissions = remember { requiredMediaPermissions() }

    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.all { it }

        val permanentlyDenied = activity?.isMediaPermissionPermanentlyDenied() == true

        viewModel.onEvent(
            MainEvents.OnMediaPermissionResult(
                granted = granted,
                permanentlyDenied = !granted && permanentlyDenied
            )
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onEvent(
            MainEvents.OnNotificationPermissionResult(
                granted = granted
            )
        )
    }

    val shareReelTitle = stringResource(R.string.share_reel)
    val unableToShareReelMessage = stringResource(R.string.unable_to_share_this_reel)


    LaunchedEffect(Unit) {
        val hasMediaPermission = context.hasMediaPermissions()

        viewModel.onEvent(
            MainEvents.OnMediaPermissionResult(
                granted = hasMediaPermission,
                permanentlyDenied = false
            )
        )

        if (!hasMediaPermission) {
            mediaPermissionLauncher.launch(mediaPermissions)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasNotificationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            viewModel.onEvent(
                MainEvents.OnNotificationPermissionResult(
                    granted = hasNotificationPermission
                )
            )

            if (!hasNotificationPermission) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        } else {
            viewModel.onEvent(
                MainEvents.OnNotificationPermissionResult(
                    granted = true
                )
            )
        }
    }

    LaunchedEffect(state.isDrawerOpen) {
        if (state.isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    val writePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.fileDialogueEvent(
                FileDialogIntent.OnRenameConfirmClicked
            )
        }
    }

    val deletePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.fileDialogueEvent(
                FileDialogIntent.OnDeletePermissionGranted
            )
        } else {
            viewModel.fileDialogueEvent(
                FileDialogIntent.OnDeletePermissionDenied
            )
        }
    }

    val moveFolderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            activity?.contentResolver?.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            viewModel.fileDialogueEvent(
                FileDialogIntent.OnMoveDestinationSelected(uri)
            )
        } else {
            viewModel.fileDialogueEvent(
                FileDialogIntent.OnMoveDestinationSelectionCancelled
            )
        }
    }

    val moveDeletePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.fileDialogueEvent(
                FileDialogIntent.OnMoveDeletePermissionGranted
            )
        } else {
            viewModel.fileDialogueEvent(
                FileDialogIntent.OnMoveDeletePermissionDenied
            )
        }
    }


    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                MainNavEvents.ExitApp -> {
                    backStack.clear()
                }

                is MainNavEvents.ShareMediaFile -> {
                    activity?.shareMediaFile(event.mediaFile)
                }

                MainNavEvents.PickMoveDestinationFolder -> {
                    moveFolderPickerLauncher.launch(null)
                }

                is MainNavEvents.RequestMediaWritePermission -> {
                    val intentSender = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MediaStore.createWriteRequest(
                            activity!!.contentResolver,
                            listOf(event.uri)
                        ).intentSender
                    } else {
                        event.pendingIntent?.intentSender
                    }

                    if (intentSender != null) {
                        writePermissionLauncher.launch(
                            Builder(intentSender).build()
                        )
                    }
                }

                is MainNavEvents.RequestMediaDeletePermission -> {
                    val intentSender = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MediaStore.createDeleteRequest(
                            activity!!.contentResolver,
                            listOf(event.uri)
                        ).intentSender
                    } else {
                        event.pendingIntent?.intentSender
                    }

                    if (intentSender != null) {
                        deletePermissionLauncher.launch(
                            Builder(intentSender).build()
                        )
                    }
                }

                is MainNavEvents.RequestMoveDeletePermission -> {
                    val intentSender = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MediaStore.createDeleteRequest(
                            activity!!.contentResolver,
                            listOf(event.uri)
                        ).intentSender
                    } else {
                        event.pendingIntent?.intentSender
                    }

                    if (intentSender != null) {
                        moveDeletePermissionLauncher.launch(
                            Builder(intentSender).build()
                        )
                    }
                }

                is MainNavEvents.PlayMediaFile -> {
                    backStack.add(
                        MediaPlayer(
                            listOf(event.mediaFile),
                            0
                        )
                    )
                }

                is MainNavEvents.OpenMediaPlayer -> {
                    backStack.add(
                        MediaPlayer(
                            event.mediaList,
                            event.startIndex
                        )
                    )
                }

                MainNavEvents.NavigateToAppLanguageSRC -> {

                    backStack.add(
                        Screen.AppLanguage(
                            FromWhichSrc.FROM_MAIN
                        )
                    )
                }

                MainNavEvents.OpenAppStoreForRating -> {
                    activity?.openAppStoreForRating()
                }
                is MainNavEvents.SendFeedbackEmail -> {
                    activity?.sendFeedbackEmail(
                        subject = event.subject,
                        message = event.message
                    )
                }
            }
        }
    }


    BackHandler {
        if (state.selectedTab != BottomNavItem.Home) {
            viewModel.onEvent(
                MainEvents.OnBackNavigationClicked(
                    activity = activity
                )
            )
        } else {
            viewModel.onEvent(OnBackClicked)
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = Color.White,
        topBar = {

            Column(
                modifier = Modifier.padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                ),
            ) {
                if (showTopBanner) {
                    BannerAdHost(
                        config = bannerState.config,
                        screen = currentBannerScreen,
                        slot = BannerAdSlot.Top,
                        modifier = Modifier
                    )
                }

                TopBar(
                    state = state,
                    viewModel = viewModel,
                    selectedTab = state.selectedTab,
                    onInfoClicked = {
                        backStack.add(Screen.DownloadGuide)
                    },
                    onPremiumClicked = {
                        backStack.add(Screen.Premium)
                    },
                    onPlayerSearchClick = {
                        viewModel.onEvent(MainEvents.OnPlayerSearchClicked)
                    },
                    onPlayerSearchClosed = {
                        viewModel.onEvent(MainEvents.OnPlayerSearchClosed)
                    },
                    onPlayerSearchQueryChanged = {
                        viewModel.onEvent(MainEvents.OnPlayerSearchQueryChanged(it))
                    },
                    onDownloadSearchClick = {
                        viewModel.onEvent(MainEvents.OnDownloadSearchClicked)
                    },
                    onDownloadSearchClosed = {
                        viewModel.onEvent(MainEvents.OnDownloadSearchClosed)
                    },
                    onDownloadSearchQueryChanged = {
                        viewModel.onEvent(MainEvents.OnDownloadSearchQueryChanged(it))
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = if (state.selectedTab != BottomNavItem.Social) {
                            WindowInsets.navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + 76.dp + bottomBannerHeight
                        } else {
                            WindowInsets.navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + bottomBannerHeight
                        },
                        top = paddingValues.calculateTopPadding()
                    )
            ) {
                when (state.selectedTab) {
                    BottomNavItem.Home -> HomeScreen(
                        fetchUrl = { url ->
                            viewModel.onEvent(FetchUrl(url))
                        },
                        downloadReel = {
                            viewModel.onEvent(OnDownLoadInBottomSheetClicked(it.videoUrl))
                        },
                        playReel = {
                            viewModel.onEvent(OnReelSelected(it))
                        },
                        onSocialClick = {
                            viewModel.onEvent(OnSocialPlatformSelected(it))
                        },
                        onReelSeeAllCLicked = {
                            viewModel.onEvent(OnTabSelected(BottomNavItem.Reels, null))
                        }
                    )

                    BottomNavItem.Player -> {
                        PlayerScreen(
                            hasMediaPermission = state.isMediaPermissionGranted,
                            searchQuery = state.playerSearchQuery,
                            isSearchActive = state.isPlayerSearchActive,
                            sendToMedia = { mediaList, startIndex ->
                                viewModel.onEvent(
                                    MainEvents.OnOpenMediaPlayerClicked(
                                        mediaList = mediaList,
                                        startIndex = startIndex,
                                        activity = activity
                                    )
                                )
                            },
                            onMoreClick = {
                                viewModel.onEvent(MainEvents.OnMediaItemInPLayerClick(it))
                            }
                        )
                    }


                    BottomNavItem.Reels -> {
                        ReelsScreen(
                            selectedReel = state.selectedReel,
                            onDownloadClick = { url ->
                                viewModel.onEvent(
                                    MainEvents.OnDownLoadInBottomSheetClicked(url)
                                )
                            },
                            onShareClick = { url ->
                                activity?.shareText(
                                    text = url,
                                    chooserTitle =shareReelTitle
                                ) ?: Toast.makeText(
                                    activity,
                                     unableToShareReelMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    BottomNavItem.Download -> {
                        DownloadScreen(
                            searchQuery = state.downloadSearchQuery,
                            isSearchActive = state.isDownloadSearchActive,
                            sendToMedia = { mediaList, startIndex ->
                                viewModel.onEvent(
                                    MainEvents.OnOpenMediaPlayerClicked(
                                        mediaList = mediaList,
                                        startIndex = startIndex,
                                        activity = activity
                                    )
                                )
                            },
                            onMoreClick = {
                                viewModel.onEvent(MainEvents.OnMediaItemInPLayerClick(it))
                            }
                        )
                    }

                    BottomNavItem.More -> {
                        MoreScreen(
                            onNotificationClick = {
                                viewModel.onNotificationEvent(NotificationEvents.OnNotificationClicked)
                            },
                            onFeedbackClick = {
                                viewModel.onEvent(MainEvents.OnFeedbackClicked)
                            },
                            onHowToDownloadClicked = {
                                backStack.add(Screen.DownloadGuide)
                            },
                            onRateUsClick = {
                                viewModel.onEvent(MainEvents.OnRateUsClicked)
                            },
                            onAppLanguageClicked = {
                                viewModel.onEvent(MainEvents.OnAppLanguageCLicked)
                            },
                            onPremiumCardClick = {
                                backStack.add(Screen.Premium)
                            }
                        )
                    }

                    BottomNavItem.Social -> {
                        Social(
                            fetchUrl = { url ->
                                viewModel.onEvent(FetchUrl(url))
                            },
                            downloadReel = {
                                viewModel.onEvent(OnDownLoadInBottomSheetClicked(it.videoUrl))
                            },
                            playReel = {
                                viewModel.onEvent(OnReelSelected(it))
                            },
                            onReelSeeAllCLicked = {
                                viewModel.onEvent(OnTabSelected(BottomNavItem.Reels, null))
                            }
                        )
                    }
                }
            }


            val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

            DisposableEffect(lifecycleOwner) {
                val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                    if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                        val hasMediaPermission = context.hasMediaPermissions()

                        viewModel.onEvent(
                            MainEvents.OnMediaPermissionResult(
                                granted = hasMediaPermission,
                                permanentlyDenied = false
                            )
                        )
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            if (state.selectedTab != BottomNavItem.Social) {
                MainBottomBar(
                    showBottomBanner = showBottomBanner,
                    config = bannerState.config,
                    screen = currentBannerScreen,
                    state = state,
                    onIntent = viewModel::onEvent,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            PrivacyDialogHost(
                isShowDialogue = state.showPolicyDialogue,
                onAgree = {
                    viewModel.onEvent(MainEvents.OnPolicyDialogueAcceptClicked)
                }
            )
            if (state.urlFetchingLoading) {
                FetchingDialog(
                    message = stringResource(R.string.fetching_video_details),
                    subMessage = stringResource(R.string.please_wait_prepare_download)
                )
            }

            if (state.showDownloadSheet && state.videoData != null) {
                DownloadBottomSheet(
                    state = DownloadSheetState(
                        isVisible = true,
                        title = state.videoData?.title,
                        thumbnail = state.videoData?.thumbnail,
                        source = state.videoData?.platform,
                        selectedIndex = state.selectedOptionIndex
                    ),
                    onDismiss = {
                        viewModel.onEvent(MainEvents.OnDismissSheet)
                    },
                    onSelect = {
                        viewModel.onEvent(MainEvents.OnOptionSelected(it))
                    },
                    onDownload = {
                        Log.d("the video to be downloaded", "${state.videoData?.downloadOptions?.firstOrNull()?.url}")
                        viewModel.onEvent(
                            MainEvents.OnDownLoadInBottomSheetClicked(
                                state.videoData?.downloadOptions?.firstOrNull()?.url ?: ""
                            )
                        )
                    }
                )
            }

            DownloadStartedDialog(
                isVisible = state.showDownloadProgressDialogue,
                onViewProgress = {
                    viewModel.onEvent(MainEvents.OnViewProgressInProgressDialogueCLicked)
                },
                onDismiss = {
                    viewModel.onEvent(MainEvents.OnDismissProgressDialogue)

                }
            )

            ExitConfirmationDialog(
                visible = state.showExitDialogue,
                onExitClick = {
                    viewModel.onEvent(OnDialogueExitClicked)
                },
                onCancelClick = {
                    viewModel.onEvent(OnDialogueCancelCLicked)
                }
            )

            if (state.showPlayerDialogue) {
                FileOptionsDialog(
                    item = state.playerMediaItem,
                    onIntent = viewModel::fileDialogueEvent
                )
            }

            NotificationSettingsDialog(
                visible = state.showNotificationDialog,
                downloadCompleteEnabled = state.draftDownloadCompleteNotificationEnabled,
                downloadFailedEnabled = state.draftDownloadFailedNotificationEnabled,
                appUpdatesEnabled = state.draftAppUpdatesNotificationEnabled,
                onDownloadCompleteChanged = {
                    viewModel.onNotificationEvent(
                        NotificationEvents.OnDraftDownloadCompleteNotificationChanged(it)
                    )
                },
                onDownloadFailedChanged = {
                    viewModel.onNotificationEvent(
                        NotificationEvents.OnDraftDownloadFailedNotificationChanged(it)
                    )
                },
                onAppUpdatesChanged = {
                    viewModel.onNotificationEvent(
                        NotificationEvents.OnDraftAppUpdatesNotificationChanged(it)
                    )
                },
                onDismiss = {
                    viewModel.onNotificationEvent(
                        NotificationEvents.OnNotificationDialogDismissed
                    )
                },
                onSaveClick = {
                    viewModel.onNotificationEvent(
                        NotificationEvents.OnNotificationSaveClicked
                    )
                }
            )


            if (state.showFileInfoDialog) {
                FileInformationDialog(
                    item = state.fileInfoMediaItem,
                    onDismiss = {
                        viewModel.fileDialogueEvent(FileDialogIntent.OnFileInfoDismiss)
                    }
                )
            }

            if (state.showRenameFileDialog) {
                RenameFileDialog(
                    fileName = state.renameDraftName,
                    errorMessage = state.renameError,
                    isLoading = state.isRenamingFile,
                    onValueChange = {
                        viewModel.fileDialogueEvent(
                            FileDialogIntent.OnRenameValueChanged(it)
                        )
                    },
                    onDismiss = {
                        viewModel.fileDialogueEvent(
                            FileDialogIntent.OnRenameDismiss
                        )
                    },
                    onConfirmClick = {
                        viewModel.fileDialogueEvent(
                            FileDialogIntent.OnRenameConfirmClicked
                        )
                    }
                )
            }

            if (state.showDeleteFileDialog) {
                DeleteFileDialog(
                    isLoading = state.isDeletingFile,
                    errorMessage = state.deleteFileError,
                    onCancelClick = {
                        viewModel.fileDialogueEvent(
                            FileDialogIntent.OnDeleteDismiss
                        )
                    },
                    onDeleteClick = {
                        viewModel.fileDialogueEvent(
                            FileDialogIntent.OnDeleteConfirmClicked
                        )
                    }
                )
            }
            MovingFileDialog(
                visible = state.isMovingFile,
                errorMessage = state.moveFileError
            )

            if (state.showFeedbackDialog) {
                SendFeedbackDialog(
                    feedback = state.feedbackMessage,
                    errorMessage = state.feedbackError,
                    isLoading = state.isSubmittingFeedback,
                    onValueChange = {
                        viewModel.onEvent(
                            MainEvents.OnFeedbackValueChanged(it)
                        )
                    },
                    onDismiss = {
                        viewModel.onEvent(
                            MainEvents.OnFeedbackDismissed
                        )
                    },
                    onSubmitClick = {
                        viewModel.onEvent(
                            MainEvents.OnFeedbackSubmitClicked
                        )
                    }
                )
            }
            if (state.showRateUsDialog) {
                RateUsDialog(
                    selectedRating = state.selectedRating,
                    errorMessage = state.rateUsError,
                    isLoading = state.isSubmittingRating,
                    onRatingSelected = {
                        viewModel.onEvent(
                            MainEvents.OnRateUsSelected(it)
                        )
                    },
                    onRateNowClick = {
                        viewModel.onEvent(
                            MainEvents.OnRateNowClicked
                        )
                    },
                    onLaterClick = {
                        viewModel.onEvent(
                            MainEvents.OnRateUsDismissed
                        )
                    }
                )
            }


            MediaPermissionRequiredDialog(
                visible = state.showMediaPermissionDialog,
                openSettings = state.shouldOpenMediaPermissionSettings,
                onAllowClick = {
                    viewModel.onEvent(MainEvents.OnMediaPermissionRequestClicked)
                    mediaPermissionLauncher.launch(mediaPermissions)
                },
                onSettingsClick = {
                    viewModel.onEvent(MainEvents.OnMediaPermissionSettingsClicked)
                    activity?.openAppSettings()
                },
                onDismiss = {
                    viewModel.onEvent(MainEvents.OnMediaPermissionDialogDismissed)
                }
            )

            if (state.showFetchFailedDialog) {
                VideoFetchFailedDialog(
                    errorMessage = state.fetchErrorMessage,
                    onRetryClick = {
                        viewModel.onEvent(MainEvents.OnFetchFailedRetryClicked)
                    },
                    onPasteNewLinkClick = {
                        viewModel.onEvent(MainEvents.OnFetchFailedPasteNewLinkClicked)
                    },
                    onHelpClick = {
                        viewModel.onEvent(MainEvents.OnFetchFailedHelpClicked)
                        backStack.add(Screen.DownloadGuide)
                    },
                    onDismiss = {
                        viewModel.onEvent(MainEvents.OnFetchFailedDismissed)
                    }
                )
            }

        }
    }


}


private fun android.app.Activity.shareMediaFile(
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
        getString(R.string.share_file, mediaFile.fileName)
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


private fun BottomNavItem.toBannerAdScreen(): BannerAdScreen {
    return when (this) {
        BottomNavItem.Home -> BannerAdScreen.Home
        BottomNavItem.Player -> BannerAdScreen.Player
        BottomNavItem.Download -> BannerAdScreen.Download
        BottomNavItem.Reels -> BannerAdScreen.Reels
        BottomNavItem.More -> BannerAdScreen.More
        BottomNavItem.Social -> BannerAdScreen.Social
    }
}


private fun android.app.Activity.shareText(
    text: String,
    chooserTitle: String,
) {
    if (text.isBlank()) return

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }

    val chooser = Intent.createChooser(
        shareIntent,
        chooserTitle
    )

    if (shareIntent.resolveActivity(packageManager) != null) {
        startActivity(chooser)
    }
}


private fun requiredMediaPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}

private fun Context.hasMediaPermissions(): Boolean {
    return requiredMediaPermissions().all { permission ->
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

private fun Activity.isMediaPermissionPermanentlyDenied(): Boolean {
    return requiredMediaPermissions().any { permission ->
        !ActivityCompat.shouldShowRequestPermissionRationale(this, permission) &&
                ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
    }
}

private fun Activity.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    startActivity(intent)
}


private fun Activity.openAppStoreForRating() {
    val marketUri = Uri.parse("market://details?id=$packageName")
    val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")

    val marketIntent = Intent(Intent.ACTION_VIEW, marketUri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }

    try {
        startActivity(marketIntent)
    } catch (_: ActivityNotFoundException) {
        startActivity(
            Intent(Intent.ACTION_VIEW, webUri)
        )
    }
}


private fun Activity.sendFeedbackEmail(
    subject: String,
    message: String
) {
    val appVersion = runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName
            ?: getString(R.string.unknown)
    }.getOrDefault(getString(R.string.unknown))

    val deviceInfo = buildString {
        appendLine()
        appendLine()
        appendLine("---")
        appendLine(getString(R.string.feedback_app_version, appVersion))
        appendLine(getString(R.string.feedback_package, packageName))
        appendLine(
            getString(
                R.string.feedback_android_version,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT
            )
        )
        appendLine(
            getString(
                R.string.feedback_device,
                Build.MANUFACTURER,
                Build.MODEL
            )
        )
    }

    val feedbackBody = message + deviceInfo

    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse(
            "mailto:${Uri.encode(SUPPORT_EMAIL)}" +
                    "?subject=${Uri.encode(subject)}" +
                    "&body=${Uri.encode(feedbackBody)}"
        )
    }

    val chooserIntent = Intent.createChooser(
        emailIntent,
        getString(R.string.send_feedback)
    )

    try {
        startActivity(chooserIntent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(
            this,
            getString(R.string.no_email_app_found),
            Toast.LENGTH_SHORT
        ).show()
    } catch (_: Exception) {
        Toast.makeText(
            this,
            getString(R.string.unable_to_open_email_app),
            Toast.LENGTH_SHORT
        ).show()
    }
}

private const val SUPPORT_EMAIL = "kzulqarnain527@gmail.com"