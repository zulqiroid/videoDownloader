package com.app.videodownloader.presentation.screens.reels.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.presentation.screens.reels.componants.ReelItem
import com.app.videodownloader.presentation.screens.reels.events.ReelsEvent
import com.app.videodownloader.presentation.screens.reels.viewModel.ReelsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelsScreen(
    selectedReel: Reel? = null,
    viewModel: ReelsViewModel = koinViewModel(),
    onDownloadClick: (String) -> Unit,
    onShareClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = state.currentIndex,
        pageCount = { state.reels.size }
    )

    LaunchedEffect(Unit) {
        viewModel.onEvent(ReelsEvent.LoadReels)
    }

    LaunchedEffect(state.reels, selectedReel) {
        viewModel.setCurrentIndex(selectedReel)

        val targetIndex = state.reels.indexOfFirst { reel ->
            reel.id == selectedReel?.id
        }

        if (targetIndex != -1 && targetIndex != pagerState.currentPage) {
            pagerState.scrollToPage(targetIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(
            ReelsEvent.OnPageChanged(pagerState.currentPage)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val reel = state.reels[page]

            ReelItem(
                reel = reel,
                isActive = page == state.currentIndex,
                onLikeClick = {
                    viewModel.onEvent(
                        ReelsEvent.OnLikeClicked(reel.id)
                    )
                },
                onShareClick = {
                    onShareClick(reel.videoUrl)
                },
                onDownloadClick = {
                    onDownloadClick(reel.videoUrl)
                }
            )
        }
    }
}