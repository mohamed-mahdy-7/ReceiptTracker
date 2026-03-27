package com.codifyr.receipttracker.util.notifications


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.codifyr.receipttracker.R

object NotificationHelper {

    private const val CHANNEL_ID = "warranty_channel"
    private const val CHANNEL_NAME = "تنبيهات الضمان"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "تنبيهات قبل انتهاء الضمان"
        }

        val manager = context.getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(channel)
    }

    fun showWarrantyNotification(
        context: Context,
        receiptTitle: String,
        daysLeft: Long
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠️ تنبيه ضمان")
            .setContentText(
                "ضمان \"$receiptTitle\" هينتهي بعد $daysLeft يوم!"
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(
            NotificationManager::class.java
        )
        manager.notify(receiptTitle.hashCode(), notification)
    }
}