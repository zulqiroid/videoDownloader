package com.app.videodownloader.presentation.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.app.videodownloader.data.manager.FullScreenAdCoordinator
import com.app.videodownloader.domain.model.ads.AdState
import com.app.videodownloader.domain.repository.ads.AppOpenAdRepository
import com.app.videodownloader.domain.usecases.ads.CanRequestAdsUseCase

class AppOpenAdLifecycleObserver(
    private val appOpenAdRepository: AppOpenAdRepository,
    private val canRequestAdsUseCase: CanRequestAdsUseCase,
    private val fullScreenAdCoordinator: FullScreenAdCoordinator
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var currentActivity: Activity? = null

    private var appWasInBackground: Boolean = false
    private var appOpenAdFlowInProgress: Boolean = false

    fun preload() {
        if (!canRequestAdsUseCase()) {
            Log.d(TAG, "App Open preload skipped: consent not ready.")
            return
        }

        appOpenAdRepository.loadAd()
    }
    override fun onStop(owner: LifecycleOwner) {
        if (appOpenAdFlowInProgress || fullScreenAdCoordinator.isAnyFullScreenAdShowing()) {
            Log.d(TAG, "App stop ignored because a full-screen ad flow is active.")
            return
        }

        appWasInBackground = true
        appOpenAdRepository.onAppMovedToBackground()

        Log.d(TAG, "App moved to background.")
    }

    override fun onResume(owner: LifecycleOwner) {
        if (fullScreenAdCoordinator.isAnyFullScreenAdShowing()) {
            Log.d(TAG, "App Open resume skipped: another full-screen ad is showing.")
            appWasInBackground = false
            return
        }

        if (!appWasInBackground) {
            Log.d(TAG, "Resume ignored because app did not come from background.")
            return
        }

        appWasInBackground = false
        showAppOpenAdOnResume()
    }

    private fun showAppOpenAdOnResume() {
        val config = appOpenAdRepository.getCurrentConfig()



        if (!canRequestAdsUseCase()) {
            Log.d(TAG, "App Open resume skipped: consent not ready.")
            return
        }

        if (fullScreenAdCoordinator.isAnyFullScreenAdShowing()) {
            Log.d(TAG, "App Open resume skipped: another full-screen ad is showing.")
            return
        }

        if (!config.enabled) {
            Log.d(TAG, "App Open resume skipped: disabled by remote config.")
            return
        }

        if (!config.showOnResume) {
            Log.d(TAG, "App Open resume skipped: showOnResume=false from remote config.")
            appOpenAdRepository.loadAd()
            return
        }

        val activity = currentActivity

        if (activity == null) {
            Log.d(TAG, "App Open skipped: current activity is null.")
            appOpenAdRepository.loadAd()
            return
        }

        appOpenAdFlowInProgress = true

        appOpenAdRepository.showAdIfAvailable(
            activity = activity,
            forceShow = false,
            onStateChanged = { state ->
                Log.d(TAG, "App resume App Open state: $state")

                when (state) {
                    AdState.Showing -> {
                        appOpenAdFlowInProgress = true
                    }

                    AdState.Dismissed,
                    is AdState.ShowFailed,
                    is AdState.Skipped,
                    is AdState.LoadFailed -> {
                        appOpenAdFlowInProgress = false
                    }

                    else -> Unit
                }
            },
            onComplete = {
                appOpenAdFlowInProgress = false
                Log.d(TAG, "App resume App Open flow completed.")
            }
        )
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) = Unit

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity === activity) {
            currentActivity = null
        }
    }

    companion object {
        private const val TAG = "AppOpenLifecycle"
    }
}