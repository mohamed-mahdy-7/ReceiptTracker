package com.codifyr.receipttracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.codifyr.receipttracker.util.notifications.NotificationHelper
import com.codifyr.receipttracker.util.notifications.WarrantyWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class ReceiptApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createNotificationChannel(this)

        scheduleWarrantyCheck()
    }

    private fun scheduleWarrantyCheck() {
        val workRequest = PeriodicWorkRequestBuilder<WarrantyWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "warranty_check",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}