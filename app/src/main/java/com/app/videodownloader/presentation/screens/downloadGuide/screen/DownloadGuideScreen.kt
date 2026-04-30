package com.app.videodownloader.presentation.screens.downloadGuide.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.presentation.screens.downloadGuide.events.DownloadGuideIntent
import com.app.videodownloader.presentation.screens.downloadGuide.state.DownloadGuideState
import com.app.videodownloader.presentation.screens.downloadGuide.state.GuideStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadGuideScreen(
    state: DownloadGuideState,
    onIntent: (DownloadGuideIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =  Color.White
                ),
                title = {
                    Text(
                        text = "How to Download",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W800,
                        color = Color(0xFF1C2024)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onIntent(DownloadGuideIntent.OnBackClicked)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color(0xFF1C2024)
                            )
                    }
                }
            )
        },
        bottomBar = {

            BottomCTA(
                onClick = {
                    onIntent(DownloadGuideIntent.OnGotItClicked)
                }
            )
        },
        containerColor = Color.White
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(state.steps) { step ->
                GuideCard(step)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun GuideCard(step: GuideStep) {

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F7F7)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier.width(65.dp).height(25.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFFF4766).copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Color(0xFFE00004),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(step.icon),
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))


            Text(
                text = step.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFF1C2024)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = step.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFF8B95A5)
            )

        }
    }
}

@Composable
fun BottomCTA(onClick: () -> Unit) {

    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(54.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE00004)
            )
        ) {
            Text(
                text = "Got It, Thanks!",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700
            )
        }
    }
}