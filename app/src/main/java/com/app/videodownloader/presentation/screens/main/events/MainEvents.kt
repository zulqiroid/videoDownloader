package com.app.videodownloader.presentation.screens.main.events

import com.app.videodownloader.presentation.screens.main.states.BottomNavItem
import com.app.videodownloader.presentation.screens.splash.events.SplashUiEvents

sealed class MainEvents {
    data class OnTabSelected(val tab: BottomNavItem) : MainEvents()


    object OnPolicyDialogueAcceptClicked: MainEvents()

    data class FetchUrl(val url: String): MainEvents()


    data class OnOptionSelected(val index: Int) : MainEvents()

    object OnDismissSheet : MainEvents()

    object OnDownLoadInBottomSheetClicked : MainEvents()
    object OnViewProgressInProgressDialogueCLicked : MainEvents()

    object OnDismissProgressDialogue : MainEvents()

    object OnBackClicked : MainEvents()

    object OnDialogueExitClicked: MainEvents()
    object OnDialogueCancelCLicked: MainEvents()

}