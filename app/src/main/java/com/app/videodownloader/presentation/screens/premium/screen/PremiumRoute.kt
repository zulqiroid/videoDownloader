package com.app.videodownloader.presentation.screens.premium.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.app.videodownloader.presentation.screens.premium.events.PremiumEffect
import com.app.videodownloader.presentation.screens.premium.state.PlanType
import com.app.videodownloader.presentation.screens.premium.viewModel.PremiumViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PremiumRoute(
    backStack: NavBackStack<NavKey>,
    viewModel: PremiumViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PremiumEffect.CloseScreen -> {
                    backStack.removeLastOrNull()
                }
                PremiumEffect.RestorePurchases -> {


                }
                is PremiumEffect.StartPurchase -> {

                }
                is PremiumEffect.ShowError -> {
                    // show snackbar
                }
                PremiumEffect.ShowSuccess -> {
                }
            }
        }
    }

    PremiumScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}