package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.privacyPolicyDialgue

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.AppButton

@Composable
fun PrivacyPolicyDialog(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onAgree: () -> Unit,
) {

    Dialog(onDismissRequest = {}) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 18.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Top Illustration
                Image(
                    painter = painterResource(id = R.drawable.ic_privacy), // your image
                    contentDescription = null,
                    modifier = Modifier
                        .size(190.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Privacy Policy",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Description

                val annotatedString = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF111827),
                            fontWeight = FontWeight.W600,
                        )
                    ) {
                        append("Downloading YouTube videos is not allowed ")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.W400,
                        )
                    ) {
                        append("due to legal restrictions. This tool supports downloads only from permitted websites apps.")
                    }
                }
                Text(
                    text = annotatedString,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))


                Text(
                    text = "Always get permission from content owners before reposting or sharing. Unauthorized downloads or reuploads may violate copyright laws and are the sole responsibility of the user.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))


                // Checkbox container
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    DoubleTickCheckbox(
                        checked = isChecked,
                        onCheckedChange = onCheckedChange
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "I have read and accept the Terms & Conditions",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                AppButton(
                    modifier = Modifier.fillMaxWidth(),
                    isEnable = isChecked,
                    text = "Agree & Continue",
                    onClick = {
                        onAgree()
                    }
                )

            }
        }
    }
}


@Composable
fun DoubleTickCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (checked) Color.Red else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (checked) Color.Red else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(3.dp)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {

        if (checked) {
            Canvas(modifier = Modifier.fillMaxSize()) {

                val stroke = Stroke(
                    width = 4f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )

                // First tick
                drawLine(
                    color = Color.White,
                    start = Offset(size.width * 0.2f, size.height * 0.55f),
                    end = Offset(size.width * 0.4f, size.height * 0.75f),
                    strokeWidth = stroke.width,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White,
                    start = Offset(size.width * 0.4f, size.height * 0.75f),
                    end = Offset(size.width * 0.75f, size.height * 0.3f),
                    strokeWidth = stroke.width,
                    cap = StrokeCap.Round
                )

                // Second tick (slightly shifted)
                drawLine(
                    color = Color.White,
                    start = Offset(size.width * 0.35f, size.height * 0.55f),
                    end = Offset(size.width * 0.55f, size.height * 0.75f),
                    strokeWidth = stroke.width,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White,
                    start = Offset(size.width * 0.55f, size.height * 0.75f),
                    end = Offset(size.width * 0.9f, size.height * 0.3f),
                    strokeWidth = stroke.width,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}