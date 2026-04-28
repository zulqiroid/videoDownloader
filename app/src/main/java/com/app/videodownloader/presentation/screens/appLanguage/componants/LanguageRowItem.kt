package com.app.videodownloader.presentation.screens.appLanguage.componants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.videodownloader.presentation.localization.AppLanguageCodes

@Composable
fun LanguageRowItem(
    language: AppLanguageCodes,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val displayName = language.label
    val description = language.description

    val placeholderIcon = painterResource(language.flag)

    Row(
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 24.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFFE00004) else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = Color.White,
            )
            .clickable { onSelect() }
            .padding(vertical = 19.dp, horizontal = 18.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {

         Image(
            painter =  placeholderIcon,
            contentDescription = displayName,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column{
            Text(
                text = displayName,
                color = Color(0xFF0F172A),
                fontSize = 16.sp,
                fontWeight = FontWeight.W600
            )
            Text(
                text = description,
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.W400
            )
        }


        Spacer(modifier = Modifier.weight(1f))

         if (isSelected) {
             Box(
                 modifier = Modifier
                     .size(24.dp)
                     .clip(CircleShape)
                     .background(
                         color = Color.White,
                     )
                     .border(
                         width = 8.dp,
                         color = Color(0xFFE00004),
                         shape = CircleShape
                     ),
             )
        }else{
             Box(
                 modifier = Modifier
                     .size(24.dp)
                     .clip(CircleShape)
                     .background(
                         color = Color.White,
                     )
                     .border(
                         width = 3.dp,
                         color = Color(0xFF64748B).copy(alpha = 0.3f),
                         shape = CircleShape
                     ),
             )
         }
    }
}