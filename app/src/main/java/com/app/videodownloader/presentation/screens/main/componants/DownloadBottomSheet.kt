package com.app.videodownloader.presentation.screens.main.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.videodownloader.R
import com.app.videodownloader.presentation.componants.AppButton
import com.app.videodownloader.presentation.screens.main.states.DownloadOptionUi
import com.app.videodownloader.presentation.screens.main.states.DownloadSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadBottomSheet(
    state: DownloadSheetState,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
    onDownload: () -> Unit,
) {

    if (!state.isVisible) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color(0xFFF9FAFB),
        dragHandle = {
            Spacer(Modifier.size(15.dp))
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // 🔹 Header
            Row(verticalAlignment = Alignment.CenterVertically) {

                AsyncImage(
                    model = state.thumbnail ?: painterResource(R.drawable.placeholder_image),
                    contentDescription = null,
                    modifier = Modifier
                        .width(130.dp)
                        .height(75.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (state.title.isNullOrBlank()) "video_m_p_4" else state.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 2
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.ic_pic_reel),
                            contentDescription = "pictorial reel",
                            tint = Color(0xFf71717A),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = state.source ?: "video.com",
                            fontSize = 12.sp,
                            color = Color(0xFF71717A),
                            fontWeight = FontWeight.W400
                        )
                    }

                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Select Quality",
                fontSize = 14.sp,
                color = Color(0xFF71717A),
                fontWeight = FontWeight.W600
            )

            Spacer(Modifier.height(12.dp))

            // 🔥 Options List
            state.options.forEachIndexed { index, item ->
                DownloadOptionItem(
                    item = item,
                    isSelected = state.selectedIndex == index,
                    onClick = { onSelect(index) }
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(20.dp))

            // 🔴 Download Button

            AppButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Download Now",
                onClick = {
                    onDownload()
                }
            )

            Spacer(Modifier.height(10.dp))

            // Cancel
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0xFF71717A),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W600
                )
            }
        }
    }
}


@Composable
fun DownloadOptionItem(
    item: DownloadOptionUi,
    isSelected: Boolean,
    onClick: () -> Unit,
) {

    val borderColor = if (isSelected) Color(0xFFE00004) else Color(0xFFE4E4E7)
    val bgColor =
        if (isSelected) Color(0xFFE00004).copy(alpha = 0.1f) else Color(0xFFF4F4F5).copy(alpha = 0.3f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Icon Circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFFE00004).copy(alpha = 0.1f) else Color.White)
                .border(
                    width = 1.dp,
                    color = if (isSelected) Color.Transparent else Color(0xFFE4E4E7)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    if (item.isMP3) {
                        R.drawable.ic_audio
                    } else {
                        if (isSelected) R.drawable.ic_vd_bundle_filled else R.drawable.ic_vd_bundle_outlined
                    }
                ),
                contentDescription = null,
                tint = if (isSelected) Color(0xFFE00004) else Color(0xFF71717A),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.quality,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF09090B),
                    fontSize = 16.sp
                )

                if (item.isPro) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Red)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("PRO", color = Color.White, fontSize = 10.sp)
                    }
                }
            }

            Text(
                text = item.format,
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFF71717A),
            )
        }

        // Radio Indicator
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
            }
        }
    }
}