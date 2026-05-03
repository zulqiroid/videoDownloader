package com.app.videodownloader.presentation.screens.more.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.R
import com.app.videodownloader.presentation.screens.more.componants.DrawerItem

@Composable
fun MoreScreen(
    onNotificationClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onHowToDownloadClicked: () -> Unit,
    onRateUsClick: () -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
    ) {paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 🔥 Premium Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp)),
            ) {
               Box(
                   modifier = Modifier
                       .fillMaxWidth()
                       .background(
                           brush = Brush.horizontalGradient(
                               colors = listOf(
                                   Color(0xFFE00004),
                                   Color(0xFF7A0002),
                               )
                           ),
                           shape = RoundedCornerShape(24.dp)
                       )
                       .clip(RoundedCornerShape(24.dp))
                       .padding(vertical = 15.dp),
                   contentAlignment = Alignment.Center
               ){
                   Row(
                       modifier = Modifier.fillMaxWidth()
                   ) {
                       Image(
                           painter = painterResource(R.drawable.big_download_arrow),
                           contentDescription = "big download arrow" ,
                           modifier = Modifier.weight(1f).alpha(0.2f)
                       )
                       Image(
                           painter = painterResource(R.drawable.premiun_diamong_img),
                           contentDescription = "premiun diamond icon ",
                           modifier = Modifier.weight(1f)
                       )
                   }
               }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "PREMIUM PLAN",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.W800
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Enjoy ad-free experience and \n faster download speeds.",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .background(Color.Yellow, RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Upgrade Now", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "GENERAL SETTINGS",
                fontSize = 10.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF8B95A5)
            )

            DrawerItem(
                title = "Languages",
                description = "Choose your preferred language",
                icon = R.drawable.ic_world,
                onClick = {
                    // TODO
                }
            )

            DrawerItem(
                title = "How to Download",
                description = "Step-by-step guide",
                icon = R.drawable.ic_how,
                onClick = {
                    onHowToDownloadClicked()
                }
            )

            DrawerItem(
                title = "Notifications",
                description = "Set up your alerts",
                icon = R.drawable.ic_bell,
                onClick = onNotificationClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "SUPPORT & SHARE",
                fontSize = 10.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF8B95A5)
            )

            DrawerItem(
                title = "Share App",
                description = "Invite friends",
                icon = R.drawable.ic_share,
                onClick = {
                    // TODO
                }
            )

            DrawerItem(
                title = "Rate Us",
                description = "Give feedback on the store",
                icon = R.drawable.ic_star,
                onClick = {
                    onRateUsClick()
                }
            )

            DrawerItem(
                title = "Feedback",
                description = "Send us your suggestions",
                icon = R.drawable.ic_mail,
                onClick = {
                     onFeedbackClick()
                }
            )

            DrawerItem(
                title = "Privacy Policy",
                description = "Legal and usage terms",
                icon = R.drawable.ic_description,
                onClick = {
                    // TODO
                }
            )
        }
    }

}