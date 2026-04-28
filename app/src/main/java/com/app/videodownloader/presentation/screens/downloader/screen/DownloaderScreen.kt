package com.app.videodownloader.presentation.screens.downloader.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.videodownloader.presentation.screens.downloader.viewModel.DownloaderViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloaderScreen(
    viewModel: DownloaderViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 100.dp, horizontal = 16.dp)
    ) {

        // 🔥 New Modern Input Bar
        LinkInputBar(
            value = state.url,
            onValueChange = viewModel::onUrlChange,
            onDownloadClick = viewModel::onDownloadClick,
            isLoading = state.isLoading
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Loading
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        // Thumbnail
        if (state.thumbnail != null) {
            AsyncImage(
                model = state.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("No Preview Available")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        state.title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Download Options
        state.downloadOptions.forEach { item ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = {
                    if (!state.isDownloading) {
                        viewModel.onQualitySelected(item.url)
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.quality)

                    if (state.isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("⬇")
                    }
                }
            }
        }

        // Error
        state.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = Color.Red)
        }
    }
}



@Composable
fun LinkInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onDownloadClick: () -> Unit,
    isLoading: Boolean
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFFF2F2F2))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔗 Left Icon
        Icon(
            imageVector = Icons.Default.Link,
            contentDescription = null,
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.width(8.dp))

        // ✏️ Text Field
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("Paste Link to Download...")
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 🔴 Download Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Red)
                .clickable(enabled = !isLoading) {
                    onDownloadClick()
                },
            contentAlignment = Alignment.Center
        ) {

            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}