package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.privacyPolicyDialgue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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

