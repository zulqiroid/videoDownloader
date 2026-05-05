package com.app.videodownloader.presentation.screens.premium.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.videodownloader.domain.model.billing.BillingConnectionState
import com.app.videodownloader.domain.model.billing.BillingPlan
import com.app.videodownloader.domain.model.billing.BillingPurchaseResult
import com.app.videodownloader.domain.usecases.billing.PremiumBillingUseCases
import com.app.videodownloader.presentation.screens.premium.events.PremiumEffect
import com.app.videodownloader.presentation.screens.premium.events.PremiumIntent
import com.app.videodownloader.presentation.screens.premium.state.Plan
import com.app.videodownloader.presentation.screens.premium.state.PlanType
import com.app.videodownloader.presentation.screens.premium.state.PremiumState
import com.app.videodownloader.presentation.screens.premium.state.defaultPreviewPlans
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PremiumViewModel(
    private val premiumBillingUseCases: PremiumBillingUseCases,
) : ViewModel() {

    private val _state = MutableStateFlow(PremiumState())
    val state: StateFlow<PremiumState> = _state

    private val _effect = Channel<PremiumEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var hasStarted = false

    init {
        observeBillingState()
        observePurchaseResults()
        observePremiumEntitlement()
    }

    fun onIntent(intent: PremiumIntent) {
        when (intent) {
            PremiumIntent.OnScreenStarted -> {
                onScreenStarted()
            }

            PremiumIntent.OnCloseClicked -> {
                sendEffect(PremiumEffect.CloseScreen)
            }

            PremiumIntent.OnRestoreClicked -> {
                restorePurchases()
            }

            is PremiumIntent.OnPlanSelected -> {
                selectPlan(intent.plan)
            }

            PremiumIntent.OnUpgradeClicked -> {
                startPurchase()
            }

            PremiumIntent.OnRetry -> {
                retry()
            }
        }
    }

    private fun onScreenStarted() {
        if (hasStarted) return
        hasStarted = true

        _state.update {
            it.copy(
                isLoading = true,
                error = null,
                message = null
            )
        }

        premiumBillingUseCases.connectBillingUseCase()
    }

    private fun observePremiumEntitlement() {
        viewModelScope.launch {
            premiumBillingUseCases
                .observePremiumEntitlementUseCase()
                .collect { entitlement ->
                    _state.update {
                        it.copy(
                            isPremiumUser = entitlement.isPremium
                        )
                    }
                }
        }
    }

    private fun observeBillingState() {
        viewModelScope.launch {
            combine(
                premiumBillingUseCases.observeBillingConnectionStateUseCase(),
                premiumBillingUseCases.observeBillingPlansUseCase()
            ) { connectionState, billingPlans ->
                connectionState to billingPlans
            }.collect { (connectionState, billingPlans) ->
                handleBillingState(
                    connectionState = connectionState,
                    billingPlans = billingPlans
                )
            }
        }
    }

    private fun handleBillingState(
        connectionState: BillingConnectionState,
        billingPlans: List<BillingPlan>,
    ) {
        val mappedPlans = billingPlans.toUiPlans()

        when (connectionState) {
            BillingConnectionState.Idle,
            BillingConnectionState.Connecting -> {
                _state.update {
                    it.copy(
                        isLoading = true,
                        plans = mappedPlans.ifEmpty { it.plans },
                        error = null
                    )
                }
            }

            BillingConnectionState.Connected -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        plans = mappedPlans.ifEmpty { defaultPreviewPlans() },
                        error = if (mappedPlans.isEmpty()) {
                            "Premium plans are not available right now."
                        } else {
                            null
                        }
                    )
                }
            }

            BillingConnectionState.Disconnected -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        plans = mappedPlans.ifEmpty { it.plans },
                        error = "Billing service disconnected. Please try again."
                    )
                }
            }

            is BillingConnectionState.Failed -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        plans = mappedPlans.ifEmpty { it.plans },
                        error = connectionState.message
                    )
                }
            }
        }
    }

    private fun observePurchaseResults() {
        viewModelScope.launch {
            premiumBillingUseCases
                .observePremiumPurchaseResultsUseCase()
                .collect { result ->
                    handlePurchaseResult(result)
                }
        }
    }

    private fun handlePurchaseResult(
        result: BillingPurchaseResult,
    ) {
        when (result) {
            BillingPurchaseResult.Success -> {
                _state.update {
                    it.copy(
                        isPurchaseInProgress = false,
                        isPremiumUser = true,
                        error = null,
                        message = "Premium activated successfully."
                    )
                }

                sendEffect(PremiumEffect.ShowSuccess)
                sendEffect(PremiumEffect.ShowMessage("Premium activated successfully."))
            }

            BillingPurchaseResult.Cancelled -> {
                _state.update {
                    it.copy(
                        isPurchaseInProgress = false,
                        error = null,
                        message = "Purchase cancelled."
                    )
                }

                sendEffect(PremiumEffect.ShowMessage("Purchase cancelled."))
            }

            BillingPurchaseResult.Pending -> {
                _state.update {
                    it.copy(
                        isPurchaseInProgress = false,
                        error = null,
                        message = "Purchase is pending. Premium will unlock after payment is completed."
                    )
                }

                sendEffect(
                    PremiumEffect.ShowMessage(
                        "Purchase is pending. Premium will unlock after payment is completed."
                    )
                )
            }

            BillingPurchaseResult.AlreadyOwned -> {
                _state.update {
                    it.copy(
                        isPurchaseInProgress = false,
                        isPremiumUser = true,
                        error = null,
                        message = "Premium already active."
                    )
                }

                sendEffect(PremiumEffect.ShowMessage("Premium already active."))
            }

            is BillingPurchaseResult.Error -> {
                _state.update {
                    it.copy(
                        isPurchaseInProgress = false,
                        error = result.message,
                        message = null
                    )
                }

                sendEffect(PremiumEffect.ShowMessage(result.message))
            }
        }
    }

    private fun restorePurchases() {
        _state.update {
            it.copy(
                isLoading = true,
                error = null,
                message = null
            )
        }

        premiumBillingUseCases.restorePremiumPurchasesUseCase()

        _state.update {
            it.copy(
                isLoading = false,
                message = "Restore request completed."
            )
        }

        sendEffect(PremiumEffect.ShowMessage("Restore request completed."))
    }

    private fun selectPlan(
        planType: PlanType,
    ) {
        if (_state.value.isPurchaseInProgress) return

        _state.update {
            it.copy(
                selectedPlan = planType,
                error = null,
                message = null
            )
        }
    }

    private fun startPurchase() {
        val currentState = _state.value

        if (currentState.isPurchaseInProgress) return

        val selectedPlanAvailable = currentState.plans.any { plan ->
            plan.type == currentState.selectedPlan && plan.price != "--"
        }

        if (!selectedPlanAvailable) {
            _state.update {
                it.copy(
                    error = "Selected plan is not available yet. Please try again."
                )
            }

            sendEffect(
                PremiumEffect.ShowMessage(
                    "Selected plan is not available yet. Please try again."
                )
            )
            return
        }

        _state.update {
            it.copy(
                isPurchaseInProgress = true,
                error = null,
                message = null
            )
        }

        sendEffect(
            PremiumEffect.StartPurchase(
                plan = currentState.selectedPlan
            )
        )
    }

    private fun retry() {
        _state.update {
            it.copy(
                isLoading = true,
                error = null,
                message = null
            )
        }

        premiumBillingUseCases.connectBillingUseCase()
    }

    private fun List<BillingPlan>.toUiPlans(): List<Plan> {
        return map { billingPlan ->
            Plan(
                type = billingPlan.planType,
                price = billingPlan.formattedPrice,
                isPopular = billingPlan.planType == PlanType.MONTHLY
            )
        }.sortedBy { plan ->
            when (plan.type) {
                PlanType.WEEKLY -> 0
                PlanType.MONTHLY -> 1
                PlanType.YEARLY -> 2
                PlanType.LIFETIME -> 3
            }
        }
    }

    private fun sendEffect(
        effect: PremiumEffect,
    ) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    override fun onCleared() {
        premiumBillingUseCases.disconnectBillingUseCase()
        super.onCleared()
    }
}