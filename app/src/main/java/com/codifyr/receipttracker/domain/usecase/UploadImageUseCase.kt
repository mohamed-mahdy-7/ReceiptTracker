package com.codifyr.receipttracker.domain.usecase

import com.codifyr.receipttracker.domain.repository.ReceiptRepository
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    suspend operator fun invoke(
        fileName: String,
        imageBytes: ByteArray
    ): Result<String> {
        return repository.uploadImage(fileName, imageBytes)
    }
}