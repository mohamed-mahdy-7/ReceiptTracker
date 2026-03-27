package com.codifyr.receipttracker.ui.receipt_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codifyr.receipttracker.domain.model.Receipt
import com.codifyr.receipttracker.domain.repository.ReceiptRepository
import com.codifyr.receipttracker.domain.usecase.DeleteReceiptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val receipt: Receipt? = null,
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: ReceiptRepository,
    private val deleteReceiptUseCase: DeleteReceiptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadReceipt(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getReceiptById(id)
                .onSuccess { receipt ->
                    _uiState.update {
                        it.copy(receipt = receipt, isLoading = false)
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

    fun deleteReceipt() {
        val receiptId = _uiState.value.receipt?.id ?: return

        viewModelScope.launch {
            deleteReceiptUseCase(receiptId)
                .onSuccess {
                    _uiState.update { it.copy(isDeleted = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }
}