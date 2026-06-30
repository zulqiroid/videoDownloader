package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.screen

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.allvideodownloader.hdvideodownloader.securevideosaver.R
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.billing.PremiumBillingUseCases
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.events.PremiumEffect
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.events.PremiumIntent
import com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.screens.premium.viewModel.PremiumViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PremiumRoute(
    backStack: NavBackStack<NavKey>,
    viewModel: PremiumViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    val premiumBillingUseCases: PremiumBillingUseCases = koinInject()

    LaunchedEffect(Unit) {
        viewModel.onIntent(PremiumIntent.OnScreenStarted)
    }

    val unableToStartPurchaseMessage = stringResource(R.string.unable_to_start_purchase_now)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PremiumEffect.CloseScreen -> {
                    backStack.removeLastOrNull()
                }

                is PremiumEffect.StartPurchase -> {
                    val currentActivity = activity

                    if (currentActivity == null) {
                        Toast.makeText(
                            activity,
                            unableToStartPurchaseMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        premiumBillingUseCases.launchPremiumPurchaseUseCase(
                            activity = currentActivity,
                            planType = effect.plan
                        )
                    }
                }

                is PremiumEffect.ShowMessage -> {
                    activity?.let {
                        Toast.makeText(
                            it,
                            effect.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                PremiumEffect.ShowSuccess -> {
                    // Later: navigate back or show success animation.
                }


            }
        }
    }

    PremiumScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}