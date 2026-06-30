package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.MediaFile
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.CloseButton
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states.FileInformationUiModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.player.states.toFileInformationUiModel

@Composable
fun FileInformationDialog(
    item: MediaFile?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (item == null) return

    val info = remember(item.filePath) {
        item.toFileInformationUiModel()
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            FileInformationContent(
                info = info,
                onDismiss = onDismiss
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FileInformationContent(
    info: FileInformationUiModel,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 18.dp, end = 18.dp)
        ) {
            CloseButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE00004)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            if (info.isVideo) {
                                R.drawable.ic_vd_bundle_filled
                            } else {
                                R.drawable.ic_downloading_audio
                            }
                        ),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.file_information),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W800,
                    color = Color(0xFF111827)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider(
            thickness = DividerDefaults.Thickness,
            color = Color(0xFFE5E7EB)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            FileInfoFullWidthItem(
                icon = R.drawable.ic_folded_page,
                label = stringResource(R.string.file_name),
                value = info.fileName
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                FileInfoGridItem(
                    icon = R.drawable.ic_media_format,
                    label = stringResource(R.string.format),
                    value = info.format,
                    modifier = Modifier.weight(1f)
                )

                FileInfoGridItem(
                    icon = R.drawable.ic_resolution,
                    label = stringResource(R.string.resolution),
                    value = info.resolution,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                FileInfoGridItem(
                    icon = R.drawable.ic_time,
                    label = stringResource(R.string.duration),
                    value = info.duration,
                    modifier = Modifier.weight(1f)
                )

                FileInfoGridItem(
                    icon = R.drawable.ic_file_stack,
                    label = stringResource(R.string.file_size),
                    value = info.fileSize,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                thickness = DividerDefaults.Thickness,
                color = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(14.dp))

            FileInfoPathItem(
                icon = R.drawable.ic_file_location,
                label = stringResource(R.string.storage_location),
                value = info.storageLocation
            )
        }
    }
}




@Composable
private fun FileInfoFullWidthItem(
    icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        FileInfoIcon(
            icon = icon,
            bgColor = Color(0xFFF3F4F6)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            FileInfoLabel(label)
            Spacer(modifier = Modifier.height(4.dp))
            FileInfoValue(
                value = value,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun FileInfoGridItem(
    icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        FileInfoIcon(
            icon = icon,
            fullIconSize =20,
            iconSize = 16
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            FileInfoLabel(label)
             FileInfoValue(value = value)
        }
    }
}

@Composable
private fun FileInfoPathItem(
    icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            FileInfoIcon(
                icon = icon,
                fullIconSize =20,
                iconSize = 16
            )

            Spacer(modifier = Modifier.width(8.dp))

            FileInfoLabel(label)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3F4F6).copy(alpha = 0.9f))
                .border(
                    width = 0.8.dp,
                    color = Color(0xFFE5E7EB).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                color = Color(0xFF6B7280),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FileInfoIcon(
    icon: Int,
    modifier: Modifier = Modifier,
    fullIconSize: Int = 40,
    iconSize: Int = 20,
    bgColor: Color? = null
) {
    Box(
        modifier = modifier
            .size(fullIconSize.dp)
            .clip(CircleShape)
            .background(bgColor ?: Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color(0xFFE00004),
            modifier = Modifier.size(iconSize.dp)
        )
    }
}

@Composable
private fun FileInfoLabel(
    text: String,
) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.W700,
        color = Color(0xFF6B7280),
        letterSpacing = 1.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun FileInfoValue(
    value: String,
    maxLines: Int = 1,
) {
    Text(
        text = value,
        fontSize = 14.sp,
        fontWeight = FontWeight.W600,
        color = Color(0xFF111827),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}