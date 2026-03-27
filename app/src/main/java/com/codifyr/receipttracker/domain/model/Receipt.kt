package com.codifyr.receipttracker.domain.model

data class Receipt(
    val id: String = "",
    val title: String = "",
    val storeName: String = "",
    val amount: Double = 0.0,
    val purchaseDate: String = "",
    val warrantyMonths: Int = 12,
    val warrantyEndDate: String = "",
    val category: String = "other",
    val imageUrl: String = "",
    val notes: String = "",
    val createdAt: String = ""
)