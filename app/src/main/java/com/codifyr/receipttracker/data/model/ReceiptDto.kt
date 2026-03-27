package com.codifyr.receipttracker.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptDto(

    val id: String = "",

    val title: String,

    @SerialName("store_name")
    val storeName: String? = null,

    val amount: Double? = null,

    @SerialName("purchase_date")
    val purchaseDate: String,

    @SerialName("warranty_months")
    val warrantyMonths: Int = 12,

    @SerialName("warranty_end_date")
    val warrantyEndDate: String? = null,

    val category: String = "other",

    @SerialName("image_url")
    val imageUrl: String? = null,

    val notes: String? = null,

    @SerialName("created_at")
    val createdAt: String = ""
)