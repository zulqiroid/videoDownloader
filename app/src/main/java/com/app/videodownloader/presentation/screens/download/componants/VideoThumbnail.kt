package com.app.videodownloader.presentation.screens.download.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.DownloadStatus
import com.app.videodownloader.presentation.screens.download.componants.DownloadTabs
import com.app.videodownloader.presentation.screens.download.states.DownloadTab
import com.app.videodownloader.presentation.screens.download.states.DownloadUiItem
import com.app.videodownloader.presentation.screens.download.viewModel.DownloadViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@Composable
fun VideoThumbnail(
    path: String?,
    height: Int = 65,
    width : Int = 100,
    overlayIcon: @Composable () -> Unit
) {

    if (path == null) {
        Box(
            modifier = Modifier
                .width(width.dp)
                .height(height.dp)
                .background(Color.Gray, RoundedCornerShape(12.dp))
        )
        return
    }

    Box(
        Modifier
            .width(width.dp)
            .height(height.dp)
            .clip(RoundedCornerShape(16.dp)),

    ){
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(File(path))
                .decoderFactory { result, options, _ ->
                    VideoFrameDecoder(result.source, options)
                }
                .size(Size.ORIGINAL)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ){
            overlayIcon()
        }


    }

}
