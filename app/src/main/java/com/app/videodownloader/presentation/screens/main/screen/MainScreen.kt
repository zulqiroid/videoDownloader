package com.app.videodownloader.presentation.screens.main.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.presentation.componants.DownloadStartedDialog
import com.app.videodownloader.presentation.componants.exitConfirmationDialogue.ExitConfirmationDialog
import com.app.videodownloader.presentation.componants.privacyPolicyDialgue.PrivacyDialogHost
import com.app.videodownloader.presentation.screens.download.screen.DownloadScreen
import com.app.videodownloader.presentation.screens.home.screen.HomeScreen
import com.app.videodownloader.presentation.screens.main.componants.DownloadBottomSheet
import com.app.videodownloader.presentation.screens.main.componants.FetchingDialog
import com.app.videodownloader.presentation.screens.main.componants.MainBottomBar
import com.app.videodownloader.presentation.screens.main.componants.TopBar
import com.app.videodownloader.presentation.screens.main.events.MainEvents
import com.app.videodownloader.presentation.screens.main.events.MainEvents.*
import com.app.videodownloader.presentation.screens.main.events.MainNavEvents
import com.app.videodownloader.presentation.screens.main.states.BottomNavItem
import com.app.videodownloader.presentation.screens.main.states.DownloadSheetState
import com.app.videodownloader.presentation.screens.main.viewModel.MainViewModel
import com.app.videodownloader.presentation.screens.more.screen.MoreScreen
import com.app.videodownloader.presentation.screens.player.screen.PlayerScreen
import com.app.videodownloader.presentation.screens.reels.screen.ReelsScreen
import com.app.videodownloader.presentation.screens.splash.events.SplashUiEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: MainViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.isDrawerOpen) {
        if (state.isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect {
            when(it){
                MainNavEvents.ExitApp -> {
                    backStack.clear()
                }
            }
        }
    }

    BackHandler {
        if (state.selectedTab != BottomNavItem.Home){
            viewModel.onEvent(OnTabSelected(BottomNavItem.Home))
        }else{
            viewModel.onEvent(OnBackClicked)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopBar(
                state = state,
                viewModel = viewModel,
                modifier = Modifier.padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                ),
                selectedTab = state.selectedTab
            )
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
                        bottom = WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding() + 76.dp,
                        top = paddingValues.calculateTopPadding()
                    )
            ) {
                when (state.selectedTab) {
                    BottomNavItem.Home -> HomeScreen(
                        fetchUrl = { url ->
                            viewModel.onEvent(FetchUrl(url))
                        }
                    )
                    BottomNavItem.Player -> PlayerScreen()
                    BottomNavItem.Reels -> ReelsScreen()
                    BottomNavItem.Download -> DownloadScreen()
                    BottomNavItem.More -> {
                        MoreScreen()
                    }
                }
            }

            MainBottomBar(
                state = state,
                onIntent = viewModel::onEvent,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            PrivacyDialogHost(
                isShowDialogue = state.showPolicyDialogue,
                onAgree = {
                    viewModel.onEvent(MainEvents.OnPolicyDialogueAcceptClicked)
                }
            )
            if (state.urlFetchingLoading) {
                FetchingDialog(
                    message = "Fetching Video...",
                    subMessage = "Analyzing link and preparing download options. This will just take a moment..."
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
                        viewModel.onEvent(MainEvents.OnDownLoadInBottomSheetClicked)
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
        }
    }

}
