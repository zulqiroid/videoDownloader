package com.app.videodownloader.presentation.screens.downloadGuide.viewModel

import androidx.lifecycle.ViewModel
import com.app.videodownloader.R
import com.app.videodownloader.presentation.screens.downloadGuide.events.DownloadGuideIntent
import com.app.videodownloader.presentation.screens.downloadGuide.state.DownloadGuideState
import com.app.videodownloader.presentation.screens.downloadGuide.state.GuideStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DownloadGuideViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        DownloadGuideState(
            steps = listOf(
                GuideStep(
                    titleRes = R.string.download_guide_step_copy_link_title,
                    descriptionRes = R.string.download_guide_step_copy_link_description,
                    icon = R.drawable.ic_audio
                ),
                GuideStep(
                    titleRes = R.string.download_guide_step_paste_analyze_title,
                    descriptionRes = R.string.download_guide_step_paste_analyze_description,
                    icon = R.drawable.ic_audio
                ),
                GuideStep(
                    titleRes = R.string.download_guide_step_select_save_title,
                    descriptionRes = R.string.download_guide_step_select_save_description,
                    icon = R.drawable.ic_audio
                )
            )
        )
    )

    val state: StateFlow<DownloadGuideState> = _state.asStateFlow()

    fun onIntent(intent: DownloadGuideIntent) {
        when (intent) {
            DownloadGuideIntent.OnBackClicked -> {
                // Navigation is handled by route/root screen.
            }

            DownloadGuideIntent.OnGotItClicked -> {
                // Navigation is handled by route/root screen.
            }
        }
    }
}