package com.app.videodownloader.domain.usecases.billing

import android.app.Activity
import com.app.videodownloader.domain.model.billing.PremiumEntitlement
import com.app.videodownloader.domain.repository.billing.BillingRepository
import com.app.videodownloader.domain.repository.billing.PremiumEntitlementRepository
import com.app.videodownloader.presentation.screens.premium.state.PlanType
import kotlinx.coroutines.flow.map


data class PremiumBillingUseCases(
    val connectBillingUseCase: ConnectBillingUseCase,
    val disconnectBillingUseCase: DisconnectBillingUseCase,
    val observeBillingPlansUseCase: ObserveBillingPlansUseCase,
    val observeBillingConnectionStateUseCase: ObserveBillingConnectionStateUseCase,
    val launchPremiumPurchaseUseCase: LaunchPremiumPurchaseUseCase,
    val restorePremiumPurchasesUseCase: RestorePremiumPurchasesUseCase,
    val observePremiumPurchaseResultsUseCase: ObservePremiumPurchaseResultsUseCase,
    val observePremiumEntitlementUseCase: ObservePremiumEntitlementUseCase,
    val observeIsPremiumUserUseCase: ObserveIsPremiumUserUseCase,
    val updatePremiumEntitlementUseCase: UpdatePremiumEntitlementUseCase,
    val clearPremiumEntitlementUseCase: ClearPremiumEntitlementUseCase,
)

class ConnectBillingUseCase(
    private val billingRepository: BillingRepository,
) {
    operator fun invoke() {
        billingRepository.connect()
    }
}

class DisconnectBillingUseCase(
    private val billingRepository: BillingRepository,
) {
    operator fun invoke() {
        billingRepository.disconnect()
    }
}


class ObserveBillingPlansUseCase(
    private val billingRepository: BillingRepository,
) {
    operator fun invoke() = billingRepository.plans
}



class ObserveBillingConnectionStateUseCase(
    private val billingRepository: BillingRepository,
) {
    operator fun invoke() = billingRepository.connectionState
}



class LaunchPremiumPurchaseUseCase(
    private val billingRepository: BillingRepository,
) {
    operator fun invoke(
        activity: Activity,
        planType: PlanType,
    ) {
        billingRepository.launchPurchase(
            activity = activity,
            planType = planType
        )
    }
}



class RestorePremiumPurchasesUseCase(
    private val billingRepository: BillingRepository,
) {
    operator fun invoke() {
        billingRepository.restorePurchases()
    }
}

class ObservePremiumPurchaseResultsUseCase(
    private val billingRepository: BillingRepository,
) {
    operator fun invoke() = billingRepository.purchaseResults
}


class ObservePremiumEntitlementUseCase(
    private val premiumEntitlementRepository: PremiumEntitlementRepository,
) {
    operator fun invoke() = premiumEntitlementRepository.observeEntitlement()
}

class UpdatePremiumEntitlementUseCase(
    private val premiumEntitlementRepository: PremiumEntitlementRepository,
) {
    suspend operator fun invoke(
        entitlement: PremiumEntitlement,
    ) {
        premiumEntitlementRepository.updateEntitlement(entitlement)
    }
}


class ClearPremiumEntitlementUseCase(
    private val premiumEntitlementRepository: PremiumEntitlementRepository,
) {
    suspend operator fun invoke() {
        premiumEntitlementRepository.clearEntitlement()
    }
}



class ObserveIsPremiumUserUseCase(
    private val premiumEntitlementRepository: PremiumEntitlementRepository,
) {
    operator fun invoke() = premiumEntitlementRepository
        .observeEntitlement()
        .map { entitlement ->
            entitlement.isPremium
        }
}