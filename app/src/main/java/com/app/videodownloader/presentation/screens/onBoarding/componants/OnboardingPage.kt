package com.app.videodownloader.presentation.screens.onBoarding.componants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.presentation.screens.onBoarding.states.OnboardingPageModel

@Composable
fun OnboardingPage(
    modifier: Modifier= Modifier,
    model: OnboardingPageModel
) {

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        model.imageRes?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()

            )
        }

        if (!model.showAd){
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = model.title,
                    fontSize = 36.sp,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.W800
                )

                Text(
                    text = model.highlight,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.W800,
                    color = Color(0xFFE00004)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = model.description,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF64748B),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W400
                )
            }
        }

        if (model.showAd) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color(0xFFF1F1F1), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Ad Placeholder")
            }
        }
    }
}