package com.app.videodownloader.presentation.screens.reels.componants

import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.app.videodownloader.presentation.screens.reels.states.ReelUi

@OptIn(UnstableApi::class)
@Composable
fun ReelItem(
    reel: ReelUi,
    isActive: Boolean,
) {
    val context = LocalContext.current

    var isPlaying by remember { mutableStateOf(true) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(reel.videoUrl))
            prepare()
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    LaunchedEffect(isActive) {
        if (isActive) {
            isPlaying = true
            exoPlayer.play()
        } else {
            isPlaying = false
            exoPlayer.pause()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isPlaying = !isPlaying

                if (isPlaying) {
                    exoPlayer.play()
                } else {
                    exoPlayer.pause()
                }
            }) {

        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 🔥 Overlay UI (like your screenshot)
        ReelOverlay(reel)

        if (!isPlaying) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(80.dp)
            )
        }
    }
}


@Composable
fun ReelOverlay(reelUi: ReelUi) {

    Box(modifier = Modifier.fillMaxSize()) {

        // Bottom Left (username + caption)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(reelUi.username, color = Color.White)
            Text(reelUi.caption, color = Color.White)
        }

        // Right Actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
            Text("105", color = Color.White)

            Spacer(Modifier.height(16.dp))

            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
            Text("Share", color = Color.White)

            Spacer(Modifier.height(16.dp))

            Icon(Icons.Default.Download, contentDescription = null, tint = Color.Red)
            Text("Download", color = Color.White)
        }
    }
}