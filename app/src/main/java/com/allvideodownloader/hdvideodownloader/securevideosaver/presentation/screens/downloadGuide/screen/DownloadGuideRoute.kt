package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.screen

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.navigation.Screen
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.events.DownloadGuideIntent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.events.DownloadGuideNavEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.downloadGuide.viewModel.DownloadGuideViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadGuideRoute(
    backStack: NavBackStack<NavKey>,
    viewModel: DownloadGuideViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                DownloadGuideNavEvents.NavigateToBackScreen -> {
                    backStack.remove(Screen.DownloadGuide)
                }
            }
        }
    }

    BackHandler {
        viewModel.onIntent(DownloadGuideIntent.OnBackClicked)
    }

    DownloadGuideScreen(
        state = state,
        onIntent = {
            viewModel.onIntent(it)
        }
    )
}