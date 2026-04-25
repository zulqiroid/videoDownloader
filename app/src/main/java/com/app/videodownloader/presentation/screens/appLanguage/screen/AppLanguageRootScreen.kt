package com.app.videodownloader.presentation.screens.appLanguage.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.presentation.componants.AppButton
import com.app.videodownloader.presentation.localization.AppLanguageCodes
import com.app.videodownloader.presentation.screens.appLanguage.componants.LanguageRowItem
import com.app.videodownloader.presentation.screens.appLanguage.componants.TopBar
import com.app.videodownloader.presentation.screens.appLanguage.viewModel.AppLanguageViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppLanguageRootScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: AppLanguageViewModel = koinViewModel()
) {
    val state by viewModel.states.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopBar(
                modifier = Modifier.padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
            )
        }
    ) {paddingValues ->

        Box(
            modifier = Modifier.padding(paddingValues)
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(AppLanguageCodes.entries){ language ->
                        LanguageRowItem(
                            language = language,
                            isSelected = state.selectedLanguage == language,
                            onSelect = {}
                        )
                    }
                }
                AppButton(
                    text = "Continue",
                    onClick = {}
                )

            }
        }

    }

}