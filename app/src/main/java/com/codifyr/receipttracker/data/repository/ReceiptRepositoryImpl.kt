package com.codifyr.receipttracker.data.repository

import com.codifyr.receipttracker.data.mapper.toDomain
import com.codifyr.receipttracker.data.mapper.toDto
import com.codifyr.receipttracker.data.model.ReceiptDto
import com.codifyr.receipttracker.domain.model.Receipt
import com.codifyr.receipttracker.domain.repository.ReceiptRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReceiptRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ReceiptRepository {

    private val table = "receipts"
    private val bucket = "receipt-images"

    // ============ جلب كل الفواتير ============
    override fun getAllReceipts(): Flow<Result<List<Receipt>>> = flow {
        try {
            val result = supabaseClient
                .from(table)
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<ReceiptDto>()
                .map { it.toDomain() }

            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // ============ جلب فاتورة واحدة ============
    override suspend fun getReceiptById(id: String): Result<Receipt> {
        return try {
            val result = supabaseClient
                .from(table)
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<ReceiptDto>()
                .toDomain()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ إضافة فاتورة ============
    override suspend fun addReceipt(receipt: Receipt): Result<Receipt> {
        return try {
            val dto = receipt.toDto()

            val result = supabaseClient
                .from(table)
                .insert(dto) {
                    select()
                }
                .decodeSingle<ReceiptDto>()
                .toDomain()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ تعديل فاتورة ============
    override suspend fun updateReceipt(receipt: Receipt): Result<Receipt> {
        return try {
            val dto = receipt.toDto()

            val result = supabaseClient
                .from(table)
                .update(dto) {
                    filter {
                        eq("id", receipt.id)
                    }
                    select()
                }
                .decodeSingle<ReceiptDto>()
                .toDomain()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ حذف فاتورة ============
    override suspend fun deleteReceipt(id: String): Result<Unit> {
        return try {
            supabaseClient
                .from(table)
                .delete {
                    filter {
                        eq("id", id)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ البحث ============
    override fun searchReceipts(query: String): Flow<Result<List<Receipt>>> =
        flow {
            try {
                val result = supabaseClient
                    .from(table)
                    .select {
                        filter {
                            or {
                                ilike("title", "%$query%")
                                ilike("store_name", "%$query%")
                                ilike("notes", "%$query%")
                            }
                        }
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<ReceiptDto>()
                    .map { it.toDomain() }

                emit(Result.success(result))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }

    // ============ فلترة حسب الفئة ============
    override fun getReceiptsByCategory(
        category: String
    ): Flow<Result<List<Receipt>>> = flow {
        try {
            val result = supabaseClient
                .from(table)
                .select {
                    filter {
                        eq("category", category)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<ReceiptDto>()
                .map { it.toDomain() }

            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // ============ رفع صورة ============
    override suspend fun uploadImage(
        fileName: String,
        imageBytes: ByteArray
    ): Result<String> {
        return try {
            supabaseClient
                .storage
                .from(bucket)
                .upload(fileName, imageBytes) {
                    upsert = true
                }

            val url = supabaseClient
                .storage
                .from(bucket)
                .publicUrl(fileName)

            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}