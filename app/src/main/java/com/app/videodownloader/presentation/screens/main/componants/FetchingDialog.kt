package com.app.videodownloader.presentation.screens.main.componants

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.videodownloader.R

@Composable
fun FetchingDialog(
    message: String,
    subMessage: String,
) {
    Dialog(onDismissRequest = { /* block dismiss */ }) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF9FAFB))
                .padding(20.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 🔥 Thumbnail blurred preview (fake placeholder)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.fetching_vd_bg),
                        contentDescription = "fetching video background",
                        contentScale = ContentScale.FillWidth
                    )


                    val infiniteTransition = rememberInfiniteTransition(label = "")

// rotation animation
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing)
                        ),
                        label = ""
                    )


                    Box(
                        Modifier.size(50.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            painter = painterResource(R.drawable.ic_loading),
                            contentDescription = "loading",
                            tint = Color(0xFFE00004),
                            modifier = Modifier
                                .size(35.dp)
                                .graphicsLayer {
                                    rotationZ = rotation
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Title
                Text(
                    text = message,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF09090B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = subMessage,
                    fontSize = 14.sp,
                    color = Color(0xFF71717A),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W400
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 🔴 Animated Progress Bar
                val infiniteTransition = rememberInfiniteTransition()
                val progress by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing)
                    )
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = Color(0xFFE00004),
                    trackColor = Color(0xFFF4F4F5),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }
        }
    }
}