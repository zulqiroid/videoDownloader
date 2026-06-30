package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.main.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import kotlinx.coroutines.delay

@Composable
fun SendFeedbackDialog(
    feedback: String,
    errorMessage: String?,
    isLoading: Boolean,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        }
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.feedback_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W800,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.feedback_message),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF6B7A90),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = feedback,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(142.dp)
                        .focusRequester(focusRequester),
                    enabled = !isLoading,
                    isError = errorMessage != null,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.feedback_placeholder),
                            color = Color(0xFF111827),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Default
                    ),
                    maxLines = 6,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF3F6FA),
                        unfocusedContainerColor = Color(0xFFF3F6FA),
                        disabledContainerColor = Color(0xFFF3F6FA).copy(alpha = 0.7f),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        errorBorderColor = Color(0xFFE00004),
                        cursorColor = Color(0xFFE00004),
                        focusedTextColor = Color(0xFF111827),
                        unfocusedTextColor = Color(0xFF111827),
                        disabledTextColor = Color(0xFF6B7280)
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = errorMessage,
                        color = Color(0xFFE00004),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color(0xFFF1F4F8),
                            contentColor = Color(0xFF5B6677),
                            disabledContainerColor = Color(0xFFF1F4F8).copy(alpha = 0.7f),
                            disabledContentColor = Color(0xFF9CA3AF)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.common_cancel),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W700
                        )
                    }

                    Button(
                        onClick = onSubmitClick,
                        enabled = !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE00004),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFE00004).copy(alpha = 0.45f),
                            disabledContentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .height(20.dp)
                                    .fillMaxWidth(0.18f)
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.common_submit),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W800
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(250)
        focusRequester.requestFocus()
    }
}