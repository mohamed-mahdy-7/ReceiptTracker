package com.codifyr.receipttracker.domain.usecase

import com.codifyr.receipttracker.domain.model.Receipt
import com.codifyr.receipttracker.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReceiptsUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    operator fun invoke(): Flow<Result<List<Receipt>>> {
        return repository.getAllReceipts()
    }
}