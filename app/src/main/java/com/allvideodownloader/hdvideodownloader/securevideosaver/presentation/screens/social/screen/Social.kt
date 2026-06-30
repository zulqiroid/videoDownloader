package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.social.screen

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
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.Reel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.ReelGrid
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.SectionHeader
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.TopSearchBar
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.social.events.SocialEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.social.viewModel.SocialViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun Social(
    viewModel: SocialViewModel = koinViewModel(),
    fetchUrl: (String) -> Unit,
    downloadReel: (Reel) -> Unit,
    playReel: (Reel) -> Unit,
    onReelSeeAllCLicked: () -> Unit
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

        SectionHeader(
            textRes = R.string.home_trending_reels_title,
            onReelSeeAllCLicked = {
                onReelSeeAllCLicked()
            }
        )

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