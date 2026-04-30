package com.app.videodownloader.presentation.screens.downloadGuide.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.presentation.screens.downloadGuide.events.DownloadGuideIntent
import com.app.videodownloader.presentation.screens.downloadGuide.viewModel.DownloadGuideViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadGuideRoute(
    backStack: NavBackStack<NavKey>,
    viewModel: DownloadGuideViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    DownloadGuideScreen(
        state = state,
        onIntent = {
            when (it) {
                DownloadGuideIntent.OnBackClicked -> {
                    backStack.removeLastOrNull()

                }
                DownloadGuideIntent.OnGotItClicked -> {
                    backStack.removeLastOrNull()
                }
            }
            viewModel.onIntent(it)
        }
    )
}