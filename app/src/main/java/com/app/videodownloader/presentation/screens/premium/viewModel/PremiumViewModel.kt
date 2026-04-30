package com.app.videodownloader.presentation.screens.premium.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.presentation.screens.premium.events.PremiumEffect
import com.app.videodownloader.presentation.screens.premium.events.PremiumIntent
import com.app.videodownloader.presentation.screens.premium.state.Plan
import com.app.videodownloader.presentation.screens.premium.state.PlanType
import com.app.videodownloader.presentation.screens.premium.state.PremiumState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PremiumViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        PremiumState(
            plans = listOf(
                Plan(PlanType.WEEKLY, "$4.99"),
                Plan(PlanType.MONTHLY, "$12.99", isPopular = true),
                Plan(PlanType.YEARLY, "$49.99")
            )
        )
    )
    val state: StateFlow<PremiumState> = _state

    private val _effect = Channel<PremiumEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: PremiumIntent) {
        when (intent) {

            PremiumIntent.OnCloseClicked -> {
                sendEffect(PremiumEffect.CloseScreen)
            }

            PremiumIntent.OnRestoreClicked -> {
                sendEffect(PremiumEffect.RestorePurchases)
            }

            is PremiumIntent.OnPlanSelected -> {
                reduce { it.copy(selectedPlan = intent.plan) }
            }

            PremiumIntent.OnUpgradeClicked -> {
                val selected = _state.value.selectedPlan
                reduce { it.copy(isPurchaseInProgress = true) }
                sendEffect(PremiumEffect.StartPurchase(selected))
            }

            PremiumIntent.OnRetry -> {
                reduce { it.copy(error = null) }
            }
        }
    }

    private fun reduce(block: (PremiumState) -> PremiumState) {
        _state.value = block(_state.value)
    }

    private fun sendEffect(effect: PremiumEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}