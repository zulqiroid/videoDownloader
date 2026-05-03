package com.app.videodownloader.presentation.screens.main.events

import android.app.Activity
import com.app.videodownloader.domain.model.MediaFile
import com.app.videodownloader.domain.model.Reel
import com.app.videodownloader.domain.model.SocialPlatform
import com.app.videodownloader.presentation.screens.main.states.BottomNavItem
import com.app.videodownloader.presentation.screens.splash.events.SplashUiEvents

sealed class MainEvents {
    data class OnTabSelected(val tab: BottomNavItem, val activity: Activity?) : MainEvents()


    object OnPolicyDialogueAcceptClicked: MainEvents()

    data class FetchUrl(val url: String): MainEvents()


    data class OnOptionSelected(val index: Int) : MainEvents()

    object OnDismissSheet : MainEvents()

    data class OnDownLoadInBottomSheetClicked(val url: String) : MainEvents()
    object OnViewProgressInProgressDialogueCLicked : MainEvents()

    object OnDismissProgressDialogue : MainEvents()

    object OnBackClicked : MainEvents()

    object OnDialogueExitClicked: MainEvents()
    object OnDialogueCancelCLicked: MainEvents()
    data class OnReelSelected(val reel : Reel): MainEvents()
    data class OnSocialPlatformSelected(val platform : SocialPlatform): MainEvents()

    data class OnMediaItemInPLayerClick(val item: MediaFile): MainEvents()


    data object OnFeedbackClicked : MainEvents()
    data object OnFeedbackDismissed : MainEvents()
    data class OnFeedbackValueChanged(val value: String) : MainEvents()
    data object OnFeedbackSubmitClicked : MainEvents()


    data object OnRateUsClicked : MainEvents()

    data class OnRateUsSelected(
        val rating: Int
    ) : MainEvents()

    data object OnRateUsDismissed : MainEvents()

    data object OnRateNowClicked : MainEvents()


    data object OnFetchFailedRetryClicked : MainEvents()

    data object OnFetchFailedPasteNewLinkClicked : MainEvents()

    data object OnFetchFailedHelpClicked : MainEvents()

    data object OnFetchFailedDismissed : MainEvents()

    data class OnOpenMediaPlayerClicked(
        val mediaList: List<MediaFile>,
        val startIndex: Int,
        val activity: android.app.Activity?
    ) : MainEvents()

    data class OnBackNavigationClicked(
        val activity: android.app.Activity?
    ) : MainEvents()

}