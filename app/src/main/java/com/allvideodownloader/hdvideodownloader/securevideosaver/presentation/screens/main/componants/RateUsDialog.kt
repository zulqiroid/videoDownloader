package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.R

@Composable
fun RateUsDialog(
    selectedRating: Int,
    errorMessage: String?,
    isLoading: Boolean,
    onRatingSelected: (Int) -> Unit,
    onRateNowClick: () -> Unit,
    onLaterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = {
            if (!isLoading) onLaterClick()
        }
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE00004).copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_heart_filled),
                        contentDescription = null,
                        tint = Color(0xFFE00004),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.rate_us_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.rate_us_message),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(26.dp))

                RatingStarsRow(
                    selectedRating = selectedRating,
                    enabled = !isLoading,
                    onRatingSelected = onRatingSelected
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFFE00004),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(34.dp))

                Button(
                    onClick = onRateNowClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE00004),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE00004).copy(alpha = 0.45f),
                        disabledContentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.rate_now_button),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(
                    onClick = onLaterClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = Color(0xFF475569),
                        disabledContainerColor = Color(0xFFF1F4F8).copy(alpha = 0.7f),
                        disabledContentColor = Color(0xFF94A3B8)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.rate_later_button),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun RatingStarsRow(
    selectedRating: Int,
    enabled: Boolean,
    onRatingSelected: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(STAR_COUNT) { index ->
            val rating = index + 1
            val selected = rating <= selectedRating

            Icon(
                painter = painterResource(R.drawable.ic_star_filled),
                contentDescription = stringResource(R.string.cd_star_rating, rating),
                tint = if (selected) Color(0xFFE00004) else Color(0xFFF1F5F9),
                modifier = Modifier
                    .size(34.dp)
                    .clickable(
                        enabled = enabled,
                        onClick = {
                            onRatingSelected(rating)
                        }
                    )
            )
        }
    }
}

private const val STAR_COUNT = 5