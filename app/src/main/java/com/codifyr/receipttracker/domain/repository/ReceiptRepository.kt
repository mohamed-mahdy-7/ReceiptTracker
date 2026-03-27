package com.codifyr.receipttracker.domain.repository

import com.codifyr.receipttracker.domain.model.Receipt
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {

    // جلب كل الفواتير
    fun getAllReceipts(): Flow<Result<List<Receipt>>>

    // جلب فاتورة واحدة
    suspend fun getReceiptById(id: String): Result<Receipt>

    // إضافة فاتورة
    suspend fun addReceipt(receipt: Receipt): Result<Receipt>

    // تعديل فاتورة
    suspend fun updateReceipt(receipt: Receipt): Result<Receipt>

    // حذف فاتورة
    suspend fun deleteReceipt(id: String): Result<Unit>

    // البحث
    fun searchReceipts(query: String): Flow<Result<List<Receipt>>>

    // فلترة حسب الفئة
    fun getReceiptsByCategory(category: String): Flow<Result<List<Receipt>>>

    // رفع صورة
    suspend fun uploadImage(
        fileName: String,
        imageBytes: ByteArray
    ): Result<String>
}