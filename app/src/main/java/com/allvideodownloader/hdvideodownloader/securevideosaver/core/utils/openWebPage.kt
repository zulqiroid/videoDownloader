package com.allvideodownloader.hdvideodownloader.securevideosaver.core.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun Activity.openWebPage(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(
            this,
            "No browser found to open link",
            Toast.LENGTH_SHORT
        ).show()
    }
}