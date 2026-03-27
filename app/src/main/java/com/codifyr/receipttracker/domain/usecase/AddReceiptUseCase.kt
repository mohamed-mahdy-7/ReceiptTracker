package com.codifyr.receipttracker.domain.usecase// domain/usecase/AddReceiptUseCase.kt

import com.codifyr.receipttracker.domain.model.Receipt
import com.codifyr.receipttracker.domain.repository.ReceiptRepository
import com.codifyr.receipttracker.util.DateUtils
import javax.inject.Inject

class AddReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    suspend operator fun invoke(receipt: Receipt): Result<Receipt> {

        // حساب تاريخ نهاية الضمان تلقائي
        val warrantyEndDate = DateUtils.calculateWarrantyEndDate(
            purchaseDate = receipt.purchaseDate,
            warrantyMonths = receipt.warrantyMonths
        )

        val receiptWithWarranty = receipt.copy(
            warrantyEndDate = warrantyEndDate
        )

        return repository.addReceipt(receiptWithWarranty)
    }
}