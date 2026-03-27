package com.codifyr.receipttracker.domain.usecase

import com.codifyr.receipttracker.domain.model.Receipt
import com.codifyr.receipttracker.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchReceiptsUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    operator fun invoke(query: String): Flow<Result<List<Receipt>>> {
        return repository.searchReceipts(query)
    }
}