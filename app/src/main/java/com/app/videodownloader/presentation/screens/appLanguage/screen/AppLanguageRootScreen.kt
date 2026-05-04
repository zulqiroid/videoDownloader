package com.app.videodownloader.presentation.screens.appLanguage.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.app.videodownloader.R
import com.app.videodownloader.domain.model.FromWhichSrc
import com.app.videodownloader.domain.model.ads.BannerAdScreen
import com.app.videodownloader.domain.model.ads.BannerAdSlot
import com.app.videodownloader.domain.model.ads.NativeAdConfig
import com.app.videodownloader.domain.model.ads.NativeAdPlacementConfig
import com.app.videodownloader.presentation.ads.banner.componants.BannerAdHost
import com.app.videodownloader.presentation.ads.banner.viewModel.BannerAdViewModel
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdHost
import com.app.videodownloader.presentation.ads.nativeAd.NativeAdSlotHelper
import com.app.videodownloader.presentation.componants.AppButton
import com.app.videodownloader.presentation.componants.exitConfirmationDialogue.ExitConfirmationDialog
import com.app.videodownloader.presentation.localization.AppLanguageCodes
import com.app.videodownloader.presentation.navigation.Screen
import com.app.videodownloader.presentation.screens.appLanguage.componants.LanguageRowItem
import com.app.videodownloader.presentation.screens.appLanguage.componants.TopBar
import com.app.videodownloader.presentation.screens.appLanguage.events.AppLanguageNavEvents
import com.app.videodownloader.presentation.screens.appLanguage.events.AppLanguageUiEvents
import com.app.videodownloader.presentation.screens.appLanguage.viewModel.AppLanguageViewModel
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

    val showTopBanner = bannerState.config.isEnabled(
        screen = bannerScreen,
        slot = BannerAdSlot.Top
    )

    val showBottomBanner = bannerState.config.isEnabled(
        screen = bannerScreen,
        slot = BannerAdSlot.Bottom
    )

    val placementKey = NativeAdConfig.APP_LANGUAGE_LIST
    val placementConfig = state.nativeAdConfig.placement(placementKey)
    val nativeAdPool = state.nativeAdPools[placementKey].orEmpty()
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
                        val startSlotKey = placementConfig?.let { config ->
                            NativeAdSlotHelper.slotKeyForIndex(
                                index = -1,
                                totalItems = languages.size,
                                config = config
                            )
                        }

                        if (placementConfig != null && startSlotKey != null) {
                            item(key = "app_language_native_ad_$startSlotKey") {
                                AppLanguageNativeAdItem(
                                    nativeAd = nativeAdPool[startSlotKey],
                                    nativeAdConfig = state.nativeAdConfig,
                                    placementConfig = placementConfig,
                                    slotKey = startSlotKey
                                )
                            }
                        }

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

                            val slotKey = placementConfig?.let { config ->
                                NativeAdSlotHelper.slotKeyForIndex(
                                    index = index,
                                    totalItems = languages.size,
                                    config = config
                                )
                            }

                            if (placementConfig != null && slotKey != null) {
                                AppLanguageNativeAdItem(
                                    nativeAd = nativeAdPool[slotKey],
                                    nativeAdConfig = state.nativeAdConfig,
                                    placementConfig = placementConfig,
                                    slotKey = slotKey
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                    ) {
                        AppButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.continue_button),
                            onClick = {
                                viewModel.onEvent(
                                    AppLanguageUiEvents.OnContinueButtonClicked(fromWhichScreen)
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
    NativeAdHost(
        nativeAd = nativeAd,
        nativeAdConfig = nativeAdConfig,
        placementConfig = placementConfig,
        placementKey = "${NativeAdConfig.APP_LANGUAGE_LIST}_$slotKey"
    )
}