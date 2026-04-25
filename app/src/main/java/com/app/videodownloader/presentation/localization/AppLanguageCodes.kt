package com.app.videodownloader.presentation.localization

import androidx.compose.ui.graphics.painter.Painter
import com.app.videodownloader.R

enum class AppLanguageCodes(val code: String, val label: String, val  flag: Int, val description: String) {
    ENGLISH("en", "English", R.drawable.english, "United States, UK"),
    URDU("ur", "Urdu", R.drawable.pakistan, "Pakistan, India"),
    HINDI("hi", "Hindi", R.drawable.india,"India"),
    ARABIC("ar", "Arabic", R.drawable.saudi_arabia, "Middle East, North Africa"),
    SPANISH("es", "Spanish", R.drawable.spain, "Spain, Latin America"),
    FRENCH("fr", "French", R.drawable.france, "France, Canada, Africa"),
    GERMAN("gr", "German", R.drawable.germany, "Germany"),
    CHINESE("ch", "Chinese", R.drawable.china, "China"),
}