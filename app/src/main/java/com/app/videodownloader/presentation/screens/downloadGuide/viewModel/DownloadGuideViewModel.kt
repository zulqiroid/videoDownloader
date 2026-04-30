package com.app.videodownloader.presentation.screens.downloadGuide.viewModel

import androidx.lifecycle.ViewModel
import com.app.videodownloader.R
import com.app.videodownloader.presentation.screens.downloadGuide.events.DownloadGuideIntent
import com.app.videodownloader.presentation.screens.downloadGuide.state.DownloadGuideState
import com.app.videodownloader.presentation.screens.downloadGuide.state.GuideStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DownloadGuideViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        DownloadGuideState(
            steps = listOf(
                GuideStep(
                    title = "Copy Video Link",
                    description = "Open the social media app of your choice, find the video you want to save, and tap the share button to copy the link.",
                    icon = R.drawable.ic_audio
                ),
                GuideStep(
                    title = "Paste & Analyze",
                    description = "Return to this app and paste the URL into the search field on the home screen. We'll fetch the video details for you automatically.",
                    icon = R.drawable.ic_audio
                ),
                GuideStep(
                    title = "Select & Save",
                    description = "Choose your preferred quality (HD, Full HD, or 4K) and tap download. The video will be saved directly to your media gallery.",
                    icon = R.drawable.ic_audio
                )
            )
        )
    )

    val state: StateFlow<DownloadGuideState> = _state

    fun onIntent(intent: DownloadGuideIntent) {
        when (intent) {
            DownloadGuideIntent.OnBackClicked -> {
                // handle navigation
            }
            DownloadGuideIntent.OnGotItClicked -> {
                // handle navigation
            }
        }
    }
}