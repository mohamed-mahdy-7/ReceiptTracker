package com.codifyr.receipttracker.util.notifications

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codifyr.receipttracker.domain.repository.ReceiptRepository
import com.codifyr.receipttracker.util.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class WarrantyWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ReceiptRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val result = repository.getAllReceipts().first()

            result.onSuccess { receipts ->
                receipts.forEach { receipt ->
                    val daysLeft = DateUtils.daysUntilWarrantyExpiry(
                        receipt.warrantyEndDate
                    )

                    // لو الضمان هينتهي خلال 30 يوم
                    if (daysLeft in 1..30) {
                        NotificationHelper.showWarrantyNotification(
                            context = context,
                            receiptTitle = receipt.title,
                            daysLeft = daysLeft
                        )
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("WarrantyWorker", "Error", e)
            Result.retry()
        }
    }
}