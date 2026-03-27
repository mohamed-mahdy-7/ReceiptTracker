package com.codifyr.receipttracker.ui.add_receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codifyr.receipttracker.domain.model.Category
import com.codifyr.receipttracker.domain.model.Receipt
import com.codifyr.receipttracker.domain.usecase.AddReceiptUseCase
import com.codifyr.receipttracker.domain.usecase.UploadImageUseCase
import com.codifyr.receipttracker.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AddReceiptUiState(
    val title: String = "",
    val storeName: String = "",
    val amount: String = "",
    val purchaseDate: String = DateUtils.today(),
    val warrantyMonths: Int = 12,
    val category: Category = Category.OTHER,
    val notes: String = "",
    val imageBytes: ByteArray? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AddReceiptUiState) return false
        return title == other.title &&
                storeName == other.storeName &&
                amount == other.amount &&
                purchaseDate == other.purchaseDate &&
                warrantyMonths == other.warrantyMonths &&
                category == other.category &&
                notes == other.notes &&
                imageBytes.contentEquals(other.imageBytes) &&
                isLoading == other.isLoading &&
                isSaved == other.isSaved &&
                error == other.error
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (imageBytes?.contentHashCode() ?: 0)
        result = 31 * result + isLoading.hashCode()
        return result
    }
}

@HiltViewModel
class AddReceiptViewModel @Inject constructor(
    private val addReceiptUseCase: AddReceiptUseCase,
    private val uploadImageUseCase: UploadImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddReceiptUiState())
    val uiState: StateFlow<AddReceiptUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onStoreNameChange(value: String) {
        _uiState.update { it.copy(storeName = value) }
    }

    fun onAmountChange(value: String) {
        // نقبل أرقام ونقطة بس
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(amount = value) }
        }
    }

    fun onDateChange(value: String) {
        _uiState.update { it.copy(purchaseDate = value) }
    }

    fun onWarrantyMonthsChange(value: Int) {
        _uiState.update { it.copy(warrantyMonths = value) }
    }

    fun onCategoryChange(value: Category) {
        _uiState.update { it.copy(category = value) }
    }

    fun onNotesChange(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun onImageCaptured(bytes: ByteArray) {
        _uiState.update { it.copy(imageBytes = bytes) }
    }

    fun saveReceipt() {
        val state = _uiState.value

        // التحقق
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "اكتب عنوان الفاتورة") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // رفع الصورة لو موجودة
            var imageUrl = ""
            state.imageBytes?.let { bytes ->
                val fileName = "receipt_${UUID.randomUUID()}.jpg"
                uploadImageUseCase(fileName, bytes)
                    .onSuccess { url -> imageUrl = url }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                error = "فشل رفع الصورة: ${error.message}",
                                isLoading = false
                            )
                        }
                        return@launch
                    }
            }

            // حفظ الفاتورة
            val receipt = Receipt(
                title = state.title,
                storeName = state.storeName,
                amount = state.amount.toDoubleOrNull() ?: 0.0,
                purchaseDate = state.purchaseDate,
                warrantyMonths = state.warrantyMonths,
                category = state.category.name.lowercase(),
                imageUrl = imageUrl,
                notes = state.notes
            )

            addReceiptUseCase(receipt)
                .onSuccess {
                    _uiState.update {
                        it.copy(isSaved = true, isLoading = false)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            error = error.message,
                            isLoading = false
                        )
                    }
                }
        }
    }

    // تعبئة البيانات من OCR
    fun fillFromScannedText(text: String) {
        // النص بييجي بالشكل: store:كارفور|amount:500.00|date:2024-01-15|
        val parts = text.split("|").filter { it.isNotBlank() }

        var store = ""
        var amount = ""
        var date = ""

        parts.forEach { part ->
            when {
                part.startsWith("store:") -> {
                    store = part.removePrefix("store:")
                }
                part.startsWith("amount:") -> {
                    amount = part.removePrefix("amount:")
                }
                part.startsWith("date:") -> {
                    date = part.removePrefix("date:")
                }
            }
        }

        _uiState.update {
            it.copy(
                storeName = store.ifBlank { it.storeName },
                amount = amount.ifBlank { it.amount },
                purchaseDate = date.ifBlank { it.purchaseDate }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}