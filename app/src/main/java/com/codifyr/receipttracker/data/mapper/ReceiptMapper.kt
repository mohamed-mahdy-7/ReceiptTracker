package com.codifyr.receipttracker.data.mapper

import com.codifyr.receipttracker.data.model.ReceiptDto
import com.codifyr.receipttracker.domain.model.Receipt

fun ReceiptDto.toDomain(): Receipt {
    return Receipt(
        id = id,
        title = title,
        storeName = storeName ?: "",
        amount = amount ?: 0.0,
        purchaseDate = purchaseDate,
        warrantyMonths = warrantyMonths,
        warrantyEndDate = warrantyEndDate ?: "",
        category = category,
        imageUrl = imageUrl ?: "",
        notes = notes ?: "",
        createdAt = createdAt
    )
}

fun Receipt.toDto(): ReceiptDto {
    return ReceiptDto(
        title = title,
        storeName = storeName.ifEmpty { null },
        amount = if (amount == 0.0) null else amount,
        purchaseDate = purchaseDate,
        warrantyMonths = warrantyMonths,
        warrantyEndDate = warrantyEndDate.ifEmpty { null },
        category = category,
        imageUrl = imageUrl.ifEmpty { null },
        notes = notes.ifEmpty { null }
    )
}