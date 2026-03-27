package com.codifyr.receipttracker.domain.usecase

import com.codifyr.receipttracker.domain.repository.ReceiptRepository
import javax.inject.Inject

class DeleteReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteReceipt(id)
    }
}