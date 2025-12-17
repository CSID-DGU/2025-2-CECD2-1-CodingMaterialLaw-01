package com.example.iot_air_quality_android.util

import android.content.Context
import androidx.work.*
import com.example.iot_air_quality_android.data.worker.SyncWorker
import java.util.concurrent.TimeUnit

object SyncScheduler {

    fun scheduleImmediateSync(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "sync_air_quality_once",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    fun schedulePeriodicSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "sync_air_quality_periodic",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}
