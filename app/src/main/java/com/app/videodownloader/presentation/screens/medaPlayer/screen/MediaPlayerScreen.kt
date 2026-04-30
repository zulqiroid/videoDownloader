package com.app.videodownloader.presentation.screens.medaPlayer.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.presentation.screens.medaPlayer.componants.AudioPlayerItem
import com.app.videodownloader.presentation.screens.medaPlayer.componants.MediaPlayerBottomSheet
import com.app.videodownloader.presentation.screens.medaPlayer.componants.VideoPlayerItem
import com.app.videodownloader.presentation.screens.medaPlayer.componants.VideoSeekBar
import com.app.videodownloader.presentation.screens.medaPlayer.componants.formatTime
import com.app.videodownloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.app.videodownloader.presentation.screens.medaPlayer.events.VideoOptionsIntent
import com.app.videodownloader.presentation.screens.medaPlayer.states.MediaPlayerState
import com.app.videodownloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaPlayerScreen(
    mediaList: List<MediaFile>,
    startIndex: Int,
    backStack: NavBackStack<NavKey>,
    viewModel: MediaPlayerViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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



    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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
                            backStack.removeLastOrNull()
                        },
                        onThreeDotsClick = {
                            viewModel.onEvent(MediaPlayerEvent.OnThreeDotsClick)

                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }

            if (state.showBottomSheet) {
                Log.d("MediaPlayer", "the medis is video: ${media.isVideo}")
                MediaPlayerBottomSheet(
                    state = media,
                    onIntent = {
                        when (it) {
                            VideoOptionsIntent.OnDeleteClicked -> {

                                viewModel.onBottomSheetIntent(VideoOptionsIntent.OnDeleteClicked)

                            }

                            VideoOptionsIntent.OnDismiss -> {
                                viewModel.onBottomSheetIntent(VideoOptionsIntent.OnDismiss)

                            }

                            VideoOptionsIntent.OnFileInfoClicked -> {

                                viewModel.onBottomSheetIntent(VideoOptionsIntent.OnFileInfoClicked)

                            }

                            VideoOptionsIntent.OnPlaybackSpeedClicked -> {

                                viewModel.onBottomSheetIntent(VideoOptionsIntent.OnPlaybackSpeedClicked)

                            }

                            VideoOptionsIntent.OnRenameClicked -> {

                                viewModel.onBottomSheetIntent(VideoOptionsIntent.OnRenameClicked)

                            }

                            VideoOptionsIntent.OnShareClicked -> {

                                viewModel.onBottomSheetIntent(VideoOptionsIntent.OnShareClicked)

                            }
                        }
                    }
                )
            }
        }

    }
}


