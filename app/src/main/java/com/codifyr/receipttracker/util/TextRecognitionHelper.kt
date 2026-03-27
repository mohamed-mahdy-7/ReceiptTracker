package com.codifyr.receipttracker.util

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object TextRecognitionHelper {

    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.Builder().build()
    )

    suspend fun recognizeText(bitmap: Bitmap): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)

            recognizer.process(image)
                .addOnSuccessListener { result ->
                    continuation.resume(Result.success(result.text))
                }
                .addOnFailureListener { error ->
                    continuation.resume(Result.failure(error))
                }
        }
    }

    // استخراج المبلغ من النص
    fun extractAmount(text: String): String? {
        val patterns = listOf(
            Regex("(?i)(total|المجموع|الإجمالي|المبلغ)[:\\s]*(\\d+[.,]\\d{2})"),
            Regex("(\\d+[.,]\\d{2})\\s*(?i)(EGP|ج\\.م|جنيه|LE|SAR|ريال)"),
            Regex("(?i)(EGP|ج\\.م|جنيه|LE|SAR|ريال)\\s*(\\d+[.,]\\d{2})"),
            Regex("\\d+[.,]\\d{2}")
        )

        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                val value = match.groupValues
                    .lastOrNull { it.matches(Regex("\\d+[.,]\\d{2}")) }
                    ?: match.value

                return value.replace(",", ".")
            }
        }
        return null
    }

    // استخراج التاريخ من النص
    fun extractDate(text: String): String? {
        val patterns = listOf(
            // 2024-01-15
            Regex("(\\d{4})-(\\d{2})-(\\d{2})"),
            // 15/01/2024 or 15-01-2024
            Regex("(\\d{2})[/\\-](\\d{2})[/\\-](\\d{4})"),
            // 01/15/2024
            Regex("(\\d{2})[/\\-](\\d{2})[/\\-](\\d{4})")
        )

        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return try {
                    formatToStandardDate(match.value)
                } catch (e: Exception) {
                    null
                }
            }
        }
        return null
    }

    private fun formatToStandardDate(dateStr: String): String {
        // لو yyyy-MM-dd
        if (dateStr.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            return dateStr
        }

        // لو dd/MM/yyyy أو dd-MM-yyyy
        val parts = dateStr.split("/", "-")
        if (parts.size == 3) {
            val day = parts[0]
            val month = parts[1]
            val year = parts[2]
            return "$year-$month-$day"
        }

        return dateStr
    }

    // استخراج اسم المحل
    fun extractStoreName(text: String): String? {
        val lines = text.split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        // أول سطر غالباً بيكون اسم المحل
        return lines.firstOrNull {
            it.length in 3..50 &&
                    !it.matches(Regex(".*\\d{4}.*")) &&
                    !it.contains("فاتورة", ignoreCase = true) &&
                    !it.contains("invoice", ignoreCase = true) &&
                    !it.contains("receipt", ignoreCase = true)
        }
    }
}