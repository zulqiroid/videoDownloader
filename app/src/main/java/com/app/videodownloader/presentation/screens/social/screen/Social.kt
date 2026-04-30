package com.app.videodownloader.presentation.screens.social.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.presentation.componants.ReelGrid
import com.app.videodownloader.presentation.componants.SectionHeader
import com.app.videodownloader.presentation.componants.TopSearchBar
import com.app.videodownloader.presentation.screens.home.componants.SocialAppsRow
import com.app.videodownloader.presentation.screens.home.events.HomeEvents
import com.app.videodownloader.presentation.screens.social.events.SocialEvents
import com.app.videodownloader.presentation.screens.social.viewModel.SocialViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun Social(
    viewModel: SocialViewModel = koinViewModel(),
    fetchUrl: (String) -> Unit,
    downloadReel: (Reel) -> Unit,
    playReel: (Reel) -> Unit,
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(SocialEvents.LoadReels)
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopSearchBar(
            value = state.url,
            onValueChange = { viewModel.onEvent(SocialEvents.OnUrlChange(it)) },
            onDownloadClick = {
                if (state.url.isNotBlank()) {
                    fetchUrl(state.url)
                }
            },
        )

        Spacer(Modifier.size(20.dp))

        SectionHeader("Trending Reels")

        ReelGrid(
            categories = state.category,
            onDownloadClick = {
                downloadReel(it)
            },
            onPlayClick = {
                playReel(it)
            }
        )
    }
}