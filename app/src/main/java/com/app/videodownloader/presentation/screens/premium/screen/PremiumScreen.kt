package com.app.videodownloader.presentation.screens.premium.screen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.presentation.screens.medaPlayer.componants.AudioPlayerItem
import com.app.videodownloader.presentation.screens.medaPlayer.componants.MediaPlayerBottomSheet
import com.app.videodownloader.presentation.screens.medaPlayer.componants.VideoPlayerItem
import com.app.videodownloader.presentation.screens.medaPlayer.componants.VideoSeekBar
import com.app.videodownloader.presentation.screens.medaPlayer.componants.formatTime
import com.app.videodownloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.app.videodownloader.presentation.screens.medaPlayer.events.VideoOptionsIntent
import com.app.videodownloader.presentation.screens.medaPlayer.states.MediaPlayerState
import com.app.videodownloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
import com.app.videodownloader.presentation.screens.premium.events.PremiumIntent
import com.app.videodownloader.presentation.screens.premium.state.PremiumState
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun PremiumScreen(
    state: PremiumState,
    onIntent: (PremiumIntent) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CircleIconButton(
                    Icons.Default.Close,
                    onClick = { onIntent(PremiumIntent.OnCloseClicked) })
                Text(
                    text = "Restore",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = WindowInsets.systemGestures.asPaddingValues().calculateBottomPadding()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE00004)
                    )
                ) {
                    Text(
                        text = "Upgrade",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(12.dp))

                // 📜 Terms
                Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy.",
                    color = Color(0xFFA0A0A0).copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W400 ,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {

            // 🌄 Background Image
            Image(
                painter = painterResource(id = R.drawable.premium_bg_img),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.85f),
                                Color.Black,
                                Color.Black,
                            )
                        )
                    )
            )

            // 📦 Content
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxHeight(0.7f)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp)

            ) {

                Column(
                    modifier = Modifier.fillMaxWidth().padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Unlimited Access",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.W700
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Access the all advanced features of downloader",
                        color = Color(0xFFF2FBFF),
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.W500
                    )
                }

                Spacer(Modifier.weight(1f))

                // ✅ Features
                FeatureItem("Ads Free!")
                FeatureItem("10x Faster Downloads")
                FeatureItem("Unlimited Downloads")
                FeatureItem("Access Full Features")

                Spacer(Modifier.height(28.dp))

                // 💳 Pricing Cards
                PricingRow()

                Spacer(Modifier.height(20.dp))

                // 📄 Small text
                Text(
                    text = "Cancel anytime. Auto-renew after trial.",
                    color = Color(0xFFA0A0A0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400 ,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CircleIconButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.25f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFFE50914)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.W700
            )
    }
}

@Composable
fun PricingRow() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        PricingCard("Weekly", "$4.99", false, Modifier.weight(1f))
        Spacer(modifier = Modifier.padding(8.dp))
        PricingCard("Monthly", "$12.99", true, Modifier.weight(1f))
        Spacer(modifier = Modifier.padding(8.dp))
        PricingCard("Yearly", "$49.99", false, Modifier.weight(1f))
    }
}

@Composable
fun PricingCard(
    title: String,
    price: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSelected) Color(0xFF1E1E1E)
                    else Color(0xFF1E1E1E).copy(alpha = 0.6f)
                )
                .border(
                    width = if (isSelected) 0.8.dp else 1.dp,
                    color = if (isSelected)Color(0xFFE00004).copy(alpha = 0.5f) else Color.Gray,
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            if (isSelected) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(
                        RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp
                        )
                    )
                        .background(Color(0xFFE00004)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Popular",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W700
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Text(
                title,
                color = if (isSelected) Color(0xFFE00004) else Color(0xFFA0A0A0),
                fontSize = 12.sp,
                fontWeight = FontWeight.W600
            )
            Spacer(Modifier.height(6.dp))
            Text(
                price,
                color = Color.White,
                 fontSize = if (isSelected) 24.sp else 18.sp,
                fontWeight = FontWeight.W700

            )

            Spacer(Modifier.height(16.dp))

        }

    }
}