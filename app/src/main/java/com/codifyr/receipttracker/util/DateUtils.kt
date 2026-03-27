package com.codifyr.receipttracker.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // حساب تاريخ نهاية الضمان
    fun calculateWarrantyEndDate(
        purchaseDate: String,
        warrantyMonths: Int
    ): String {
        return try {
            val date = LocalDate.parse(purchaseDate, formatter)
            val endDate = date.plusMonths(warrantyMonths.toLong())
            endDate.format(formatter)
        } catch (e: Exception) {
            ""
        }
    }

    // حساب الأيام المتبقية للضمان
    fun daysUntilWarrantyExpiry(warrantyEndDate: String): Long {
        return try {
            val endDate = LocalDate.parse(warrantyEndDate, formatter)
            val today = LocalDate.now()
            ChronoUnit.DAYS.between(today, endDate)
        } catch (e: Exception) {
            0L
        }
    }

    // هل الضمان لسه ساري؟
    fun isWarrantyValid(warrantyEndDate: String): Boolean {
        return daysUntilWarrantyExpiry(warrantyEndDate) > 0
    }

    // تحويل التاريخ لصيغة عرض
    fun formatForDisplay(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString, formatter)
            val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            date.format(displayFormatter)
        } catch (e: Exception) {
            dateString
        }
    }

    // تاريخ اليوم
    fun today(): String {
        return LocalDate.now().format(formatter)
    }
}