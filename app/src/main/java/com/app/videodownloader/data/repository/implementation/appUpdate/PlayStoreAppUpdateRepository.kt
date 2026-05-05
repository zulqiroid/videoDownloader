package com.app.videodownloader.data.repository.implementation.appUpdate

import android.app.Activity
import android.content.Context
import com.app.videodownloader.domain.model.appUpdate.AppUpdatePolicy
import com.app.videodownloader.domain.model.appUpdate.AppUpdateResult
import com.app.videodownloader.domain.repository.appUpdate.AppUpdateRepository
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PlayStoreAppUpdateRepository(
    context: Context,
    private val policy: AppUpdatePolicy = AppUpdatePolicy(),
) : AppUpdateRepository {

    private val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(context.applicationContext)

    override suspend fun checkAndStartImmediateUpdateIfRequired(
        activity: Activity,
    ): AppUpdateResult {
        return withContext(Dispatchers.Main.immediate) {
            runCatching {
                val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

                val isUpdateAvailable =
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

                if (!isUpdateAvailable) {
                    return@withContext AppUpdateResult.UpdateNotAvailable
                }

                val isImmediateAllowed =
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

                if (!isImmediateAllowed) {
                    return@withContext AppUpdateResult.NoUpdateRequired
                }

                val updatePriority = appUpdateInfo.updatePriority()
                val stalenessDays = appUpdateInfo.clientVersionStalenessDays() ?: 0

                val shouldForceImmediateUpdate =
                    updatePriority >= policy.immediateUpdatePriorityThreshold ||
                            stalenessDays >= policy.immediateUpdateStalenessDaysThreshold

                if (!shouldForceImmediateUpdate) {
                    return@withContext AppUpdateResult.NoUpdateRequired
                }

                appUpdateManager.startUpdateFlow(
                    appUpdateInfo,
                    activity,
                    AppUpdateOptions
                        .newBuilder(AppUpdateType.IMMEDIATE)
                        .build()
                )

                AppUpdateResult.UpdateStarted
            }.getOrElse { throwable ->
                AppUpdateResult.Failed(
                    message = throwable.message ?: "Unable to check app update"
                )
            }
        }
    }

    override fun completeFlexibleUpdateIfDownloaded() {
        appUpdateManager.completeUpdate()
    }
}