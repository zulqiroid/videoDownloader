package com.app.videodownloader.presentation.screens.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.presentation.screens.home.componants.ReelGrid
import com.app.videodownloader.presentation.screens.home.componants.SocialAppsRow
import com.app.videodownloader.presentation.screens.home.componants.TopSearchBar
import com.app.videodownloader.presentation.screens.home.events.HomeEvents
import com.app.videodownloader.presentation.screens.home.viewModel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    fetchUrl: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvents.LoadReels)
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopSearchBar(
            value = state.url,
            onValueChange = { viewModel.onEvent(HomeEvents.OnUrlChange(it)) },
            onDownloadClick = {
                if (state.url.isNotBlank()) {
                    fetchUrl(state.url)
                }
            },
        )

        Spacer(Modifier.size(20.dp))

        SocialAppsRow(
            state = state
        )

        SectionHeader("Trending Reels")

        ReelGrid(
            categories = state.category,
            onDownloadClick = {
            }
        )
    }
}

@Composable
fun SectionHeader(text: String, supportingText: String = "See All") {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.W700
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = supportingText,
                fontSize = 16.sp,
                color = Color(0xFFE00004),
                fontWeight = FontWeight.W600
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Arrow forward",
                tint = Color(0xFFE00004),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}