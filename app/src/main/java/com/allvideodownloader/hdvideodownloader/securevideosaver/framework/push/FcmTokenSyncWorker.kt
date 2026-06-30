package com.allvideodownloader.hdvideodownloader.securevideosaver.framework.push

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.allvideodownloader.hdvideodownloader.securevideosaver.domain.usecases.push.SyncFcmTokenUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class FcmTokenSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val syncFcmTokenUseCase: SyncFcmTokenUseCase by inject()

    override suspend fun doWork(): ListenableWorker.Result {
        return syncFcmTokenUseCase()
            .fold(
                onSuccess = {
                    ListenableWorker.Result.success()
                },
                onFailure = {
                    if (runAttemptCount < MAX_RETRY_COUNT) {
                        ListenableWorker.Result.retry()
                    } else {
                        ListenableWorker.Result.failure()
                    }
                }
            )
    }

    companion object {
        private const val WORK_NAME = "fcm_token_sync_worker"
        private const val MAX_RETRY_COUNT = 3

        fun enqueue(
            context: Context
        ) {
            val request = OneTimeWorkRequestBuilder<FcmTokenSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    request
                )
        }
    }
}