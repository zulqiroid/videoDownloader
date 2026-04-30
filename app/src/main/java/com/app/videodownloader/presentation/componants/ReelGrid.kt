package com.app.videodownloader.presentation.componants

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.app.videodownloader.data.local.dataSource.ThumbnailCache
import com.app.videodownloader.data.local.dataSource.VideoThumbnailUtil
import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.domain.model.ReelCategory
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.app.videodownloader.R

@Composable
fun rememberVideoThumbnail(videoUrl: String): State<Bitmap?> {

    return produceState<Bitmap?>(initialValue = ThumbnailCache.get(videoUrl), videoUrl) {

        // If already cached → don't regenerate
        if (value != null) return@produceState

        value = VideoThumbnailUtil.getThumbnail(videoUrl)
    }
}
@Composable
fun ReelGrid(
    categories: List<ReelCategory>,
    onDownloadClick: (Reel) -> Unit,
    onPlayClick: (Reel) -> Unit,
) {

    val reels = categories.flatMap { it.reels }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(reels) { reel ->
            ReelItem(
                reel = reel,
                onDownloadClick = {
                    onDownloadClick(it)
                },
                onPlayClick = {
                    onPlayClick(it)
                }
            )
        }
    }
}

@Composable
fun ReelItem(
    reel: Reel,
    onDownloadClick: (Reel) -> Unit,
    onPlayClick: (Reel) -> Unit
) {
    val thumbnailState = rememberVideoThumbnail(reel.videoUrl)

    Box {
        val bitmap = thumbnailState.value

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder (important for UX)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(45.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.4f))
                .clickable {
                    onPlayClick(reel)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(25.dp)
            )
        }


        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 15.dp, vertical = 10.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFE00004))
                .clickable {
                    onDownloadClick(reel)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_download1),
                contentDescription = "download icon",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}