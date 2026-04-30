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

}