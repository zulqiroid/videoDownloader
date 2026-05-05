package com.app.videodownloader.presentation.screens.player.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.videodownloader.R
import com.app.videodownloader.presentation.componants.CloseButton
import kotlinx.coroutines.delay

@Composable
fun RenameFileDialog(
    fileName: String,
    errorMessage: String?,
    isLoading: Boolean,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        }
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                RenameDialogHeader(
                    onDismiss = onDismiss,
                    dismissEnabled = !isLoading
                )

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    color = Color(0xFFE5E7EB)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.new_file_name),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.W800,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fileName,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        enabled = !isLoading,
                        singleLine = true,
                        isError = errorMessage != null,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE00004),
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            errorBorderColor = Color(0xFFE00004),
                            focusedTextColor = Color(0xFF111827),
                            unfocusedTextColor = Color(0xFF111827),
                            cursorColor = Color(0xFFE00004)
                        ),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.enter_file_name),
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp
                            )
                        }
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = errorMessage,
                            color = Color(0xFFE00004),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            enabled = !isLoading,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color(0xFFF1F4F8),
                                contentColor = Color(0xFF5B6677)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.cancel),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W700
                            )
                        }

                        Button(
                            onClick = onConfirmClick,
                            enabled = !isLoading,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
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
                                    text = stringResource(R.string.rename),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W800
                                )
                            }
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

@Composable
private fun RenameDialogHeader(
    onDismiss: () -> Unit,
    dismissEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 18.dp, end = 18.dp)
    ) {
        CloseButton(
            onClick = {
                if (dismissEnabled) onDismiss()
            },
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE00004)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.rename_file),
                fontSize = 18.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.rename_file_description),
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                color = Color(0xFF8B95A5)
            )
        }
    }
}
