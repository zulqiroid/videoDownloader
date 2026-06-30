package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.FromWhichSrc
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.BannerAdScreen
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.BannerAdSlot
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPlacementConfig
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.ads.NativeAdPosition
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.banner.componants.BannerAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.banner.viewModel.BannerAdViewModel
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdHost
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.ads.nativeAd.NativeAdSlotHelper
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.AppButton
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.componants.exitConfirmationDialogue.ExitConfirmationDialog
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.localization.AppLanguageCodes
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.navigation.Screen
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.componants.LanguageRowItem
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.componants.TopBar
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.events.AppLanguageNavEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.events.AppLanguageUiEvents
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.appLanguage.viewModel.AppLanguageViewModel
import com.google.android.gms.ads.nativead.NativeAd
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppLanguageRootScreen(
    fromWhichScreen: FromWhichSrc,
    backStack: NavBackStack<NavKey>,
    viewModel: AppLanguageViewModel = koinViewModel(),
    bannerAdViewModel: BannerAdViewModel = koinViewModel(),
) {
    val state by viewModel.states.collectAsState()
    val bannerState by bannerAdViewModel.state.collectAsState()

    val bannerScreen = BannerAdScreen.AppLanguage

    val canShowAds = !state.isPremiumUser

    val showTopBanner = canShowAds && bannerState.config.isEnabled(
        screen = bannerScreen,
        slot = BannerAdSlot.Top
    )

    val showBottomBanner = canShowAds && bannerState.config.isEnabled(
        screen = bannerScreen,
        slot = BannerAdSlot.Bottom
    )

    val placementKey = NativeAdConfig.APP_LANGUAGE_LIST


    val placementConfig = if (canShowAds) {
        state.nativeAdConfig.placement(placementKey)
    } else {
        null
    }


    val languages = AppLanguageCodes.entries
    val activity = LocalActivity.current

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                AppLanguageNavEvents.NavigateToOnBoarding -> {
                    activity?.recreate()
                    backStack.clear()
                    backStack.add(Screen.OnBoarding)
                }

                AppLanguageNavEvents.ExitApp -> {
                    backStack.clear()
                }

                AppLanguageNavEvents.NavigateToBack -> {
                    backStack.removeLastOrNull()
                }

                AppLanguageNavEvents.RecreateActivity ->{
                    activity?.recreate()
                    backStack.removeLastOrNull()
                }
            }
        }
    }

    BackHandler {
        if (fromWhichScreen == FromWhichSrc.FROM_MAIN) {
            viewModel.onEvent(AppLanguageUiEvents.OnNavigateBack)
        } else {
            viewModel.onEvent(AppLanguageUiEvents.OnBackClicked)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            Column(
                modifier = Modifier.padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding()
                )
            ) {
                if (showTopBanner) {
                    BannerAdHost(
                        config = bannerState.config,
                        screen = bannerScreen,
                        slot = BannerAdSlot.Top
                    )
                }
                TopBar(
                    modifier = Modifier
                )

                if (placementConfig?.position == NativeAdPosition.Top){
                    NativeAdHost(
                        nativeAd = state.nativeAds[placementKey],
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = placementConfig,
                        placementKey = placementKey
                    )
                }

            }
        },
        bottomBar = {

            Column(
                modifier = Modifier.navigationBarsPadding()
            ) {

                if (placementConfig?.position == NativeAdPosition.Bottom) {
                    NativeAdHost(
                        nativeAd = state.nativeAds[placementKey],
                        nativeAdConfig = state.nativeAdConfig,
                        placementConfig = placementConfig,
                        placementKey = placementKey
                    )
                }

                AppButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.continue_button),
                    onClick = {
                        viewModel.onEvent(
                            AppLanguageUiEvents.OnContinueButtonClicked(
                                activity = activity!!,
                                fromWhichScreen
                            )
                        )
                    }
                )

                if (showBottomBanner) {
                    BannerAdHost(
                        config = bannerState.config,
                        screen = bannerScreen,
                        slot = BannerAdSlot.Bottom
                    )
                }


            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {


                        itemsIndexed(
                            items = languages,
                            key = { _, language -> language.name }
                        ) { index, language ->

                            LanguageRowItem(
                                language = language,
                                isSelected = state.selectedLanguage == language,
                                onSelect = {
                                    viewModel.onEvent(
                                        AppLanguageUiEvents.OnLanguageItemClicked(language)
                                    )
                                }
                            )

                        }
                    }


                }
            }

            ExitConfirmationDialog(
                visible = state.showExitDialogue,
                onExitClick = {
                    viewModel.onEvent(AppLanguageUiEvents.OnDialogueExitClicked)
                },
                onCancelClick = {
                    viewModel.onEvent(AppLanguageUiEvents.OnDialogueCancelCLicked)
                }
            )
        }
    }
}

@Composable
private fun AppLanguageNativeAdItem(
    nativeAd: NativeAd?,
    nativeAdConfig: NativeAdConfig,
    placementConfig: NativeAdPlacementConfig,
    slotKey: String,
) {

}