package com.app.videodownloader.presentation.componants.privacyPolicyDialgue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PrivacyDialogHost(
    isShowDialogue : Boolean = false,
    onAgree: () -> Unit,
) {

     var isChecked by remember { mutableStateOf(false) }

    if (isShowDialogue) {
        PrivacyPolicyDialog(
            isChecked = isChecked,
            onCheckedChange = { isChecked = it },
            onAgree = {
                onAgree()
            },

        )
    }
}

