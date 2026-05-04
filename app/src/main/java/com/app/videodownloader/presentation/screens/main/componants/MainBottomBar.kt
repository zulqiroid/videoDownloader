package com.app.videodownloader.presentation.screens.main.componants

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.ads.BannerAdConfig
import com.app.videodownloader.domain.model.ads.BannerAdScreen
import com.app.videodownloader.domain.model.ads.BannerAdSlot
import com.app.videodownloader.presentation.ads.banner.componants.BannerAdHost
import com.app.videodownloader.presentation.screens.main.events.MainEvents
import com.app.videodownloader.presentation.screens.main.states.BottomNavItem
import com.app.videodownloader.presentation.screens.main.states.MainState

@Composable
fun MainBottomBar(
    showBottomBanner: Boolean,
    config: BannerAdConfig,
    screen: BannerAdScreen,
    state: MainState,
    onIntent: (MainEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Reels,
        BottomNavItem.Player,
        BottomNavItem.Download,
        BottomNavItem.More,
    )

    val activity = LocalActivity.current

    val infiniteTransition = rememberInfiniteTransition(label = "")

// animate radius
    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

// animate spread
    val animatedSpread by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow( // 👈 FIRST
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), // 👈 match shape
                shadow = androidx.compose.ui.graphics.shadow.Shadow(
                    radius = 10.dp,
                    spread = 2.dp,
                    color = Color.Gray,
                    offset = DpOffset(x = 0.dp, y = 0.dp) // 👈 slight offset
                )
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .navigationBarsPadding(),

    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding( horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            items.forEach { item ->

                if (item == BottomNavItem.Player) {

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {

                        /*  val composition by rememberLottieComposition(
                              LottieCompositionSpec.RawRes(R.raw.play_button_bg_lottie)
                          )

                          val progress by animateLottieCompositionAsState(
                              composition = composition,
                              iterations = LottieConstants.IterateForever
                          )

                          LottieAnimation(
                              composition = composition,
                              progress = { progress },
                              modifier = Modifier.size(100.dp).border(
                                  color = Color.Yellow,
                                  width = 1.dp
                              ) // match outer size
                          )
      */

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .dropShadow(
                                    shape = CircleShape,
                                    shadow = androidx.compose.ui.graphics.shadow.Shadow(
                                        radius = animatedRadius.dp,
                                        spread = animatedSpread.dp,
                                        color = Color(0xFFE00004).copy(alpha = 0.4f),
                                        offset = DpOffset(0.dp, 0.dp)
                                    )
                                )
                                .clip(CircleShape)
                                .background(Color(0xFFE00004))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    onIntent(MainEvents.OnTabSelected(item, activity))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_play),
                                contentDescription = "play",
                                tint = Color.White,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }

                }else if(item == BottomNavItem.Player){} else {
                    val selected = state.selectedTab == item

                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.15f else 1f,
                        label = "scaleAnim"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            /* .graphicsLayer {
                                 scaleX = scale
                                 scaleY = scale
                             }*/
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onIntent(MainEvents.OnTabSelected(item, activity))
                            }
                            .padding(horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                if (selected) item.iconSelected else item.iconNotSelected
                            ),
                            contentDescription = item.title,
                            tint = if (selected) Color(0xFFE00004) else Color(0xFF6B7280),
                            modifier = Modifier.size(36.dp)
                        )

                        Text(
                            text = item.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (selected) Color(0xFFE00004) else Color(0xFF6B7280)
                        )
                    }

                    Spacer(modifier = Modifier.size(10.dp))
                }

            }
        }
        if (showBottomBanner) {
            BannerAdHost(
                config = config,
                screen = screen,
                slot = BannerAdSlot.Bottom
            )
        }
    }

}