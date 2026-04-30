package com.app.videodownloader.presentation.screens.download.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.videodownloader.domain.model.DownloadStatus
import com.app.videodownloader.presentation.screens.download.states.DownloadTab
import com.app.videodownloader.presentation.screens.download.states.DownloadUiItem
import com.app.videodownloader.presentation.screens.download.viewModel.DownloadViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadTabs(
    selected: DownloadTab,
    downloadingCount: Int,
    completedCount: Int,
    onTabSelected: (DownloadTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF1F3F5), RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {

        TabItem(
            title = "Downloading ($downloadingCount)",
            selected = selected == DownloadTab.DOWNLOADING,
            onClick = { onTabSelected(DownloadTab.DOWNLOADING) }
        )

        TabItem(
            title = "Completed ($completedCount)",
            selected = selected == DownloadTab.COMPLETED,
            onClick = { onTabSelected(DownloadTab.COMPLETED) }
        )
    }
}

@Composable
private fun RowScope.TabItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color(0xFFE00004) else Color(0xFF868E96),
            fontWeight = FontWeight.W700,
            fontSize = 14.sp
        )
    }
}