package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.componants

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.AppButton
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.states.DownloadOptionUi
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.states.DownloadSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadBottomSheet(
    state: DownloadSheetState,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
    onDownload: () -> Unit,
) {
    if (!state.isVisible) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color(0xFFF9FAFB),
        dragHandle = {
            Spacer(
                modifier = Modifier
                    .width(48.dp)
                    .height(8.dp)
                    .background(Color(0xFFF3F4F6))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = state.thumbnail ?: painterResource(R.drawable.placeholder_image),
                    contentDescription = null,
                    modifier = Modifier
                        .width(130.dp)
                        .height(75.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = state.title?.takeIf { it.isNotBlank() }
                            ?: stringResource(R.string.download_sheet_default_title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_pic_reel),
                            contentDescription = stringResource(R.string.cd_pictorial_reel),
                            tint = Color(0xFF71717A),
                            modifier = Modifier.size(15.dp)
                        )

                        Text(
                            text = state.source?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.download_sheet_default_source),
                            fontSize = 12.sp,
                            color = Color(0xFF71717A),
                            fontWeight = FontWeight.W400
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.download_sheet_select_quality),
                fontSize = 14.sp,
                color = Color(0xFF71717A),
                fontWeight = FontWeight.W600
            )

            Spacer(modifier = Modifier.height(12.dp))

            state.options.forEachIndexed { index, item ->
                DownloadOptionItem(
                    item = item,
                    isSelected = state.selectedIndex == index,
                    onClick = { onSelect(index) }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.download_sheet_download_now),
                onClick = onDownload
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.common_cancel),
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
    val bgColor = if (isSelected) {
        Color(0xFFE00004).copy(alpha = 0.1f)
    } else {
        Color(0xFFF4F4F5).copy(alpha = 0.3f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color(0xFFE00004).copy(alpha = 0.1f) else Color.White
                )
                .border(
                    width = 1.dp,
                    color = if (isSelected) Color.Transparent else Color(0xFFE4E4E7),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    if (item.isMP3) {
                        R.drawable.ic_audio
                    } else {
                        if (isSelected) {
                            R.drawable.ic_vd_bundle_filled
                        } else {
                            R.drawable.ic_vd_bundle_outlined
                        }
                    }
                ),
                contentDescription = null,
                tint = if (isSelected) Color(0xFFE00004) else Color(0xFF71717A),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.quality,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF09090B),
                    fontSize = 16.sp
                )

                if (item.isPro) {
                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Red)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.common_pro),
                            color = Color.White,
                            fontSize = 10.sp
                        )
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