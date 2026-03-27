package com.codifyr.receipttracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codifyr.receipttracker.domain.model.Category
import com.codifyr.receipttracker.domain.model.Receipt
import com.codifyr.receipttracker.domain.usecase.DeleteReceiptUseCase
import com.codifyr.receipttracker.domain.usecase.GetReceiptsUseCase
import com.codifyr.receipttracker.domain.usecase.SearchReceiptsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val receipts: List<Receipt> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: Category? = null,
    val isSearchActive: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getReceiptsUseCase: GetReceiptsUseCase,
    private val searchReceiptsUseCase: SearchReceiptsUseCase,
    private val deleteReceiptUseCase: DeleteReceiptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadReceipts()
    }

    fun loadReceipts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getReceiptsUseCase().collect { result ->
                result.onSuccess { receipts ->
                    _uiState.update {
                        it.copy(
                            receipts = receipts,
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            error = error.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            loadReceipts()
            return
        }

        viewModelScope.launch {
            searchReceiptsUseCase(query).collect { result ->
                result.onSuccess { receipts ->
                    _uiState.update {
                        it.copy(receipts = receipts)
                    }
                }
            }
        }
    }

    fun onCategorySelected(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }

        if (category == null) {
            loadReceipts()
            return
        }

        // فلترة محلية من القائمة الحالية
        viewModelScope.launch {
            getReceiptsUseCase().collect { result ->
                result.onSuccess { allReceipts ->
                    val filtered = allReceipts.filter {
                        it.category.equals(category.name, ignoreCase = true)
                    }
                    _uiState.update {
                        it.copy(receipts = filtered)
                    }
                }
            }
        }
    }

    fun toggleSearch() {
        _uiState.update {
            it.copy(
                isSearchActive = !it.isSearchActive,
                searchQuery = ""
            )
        }
        if (!_uiState.value.isSearchActive) {
            loadReceipts()
        }
    }

    fun deleteReceipt(id: String) {
        viewModelScope.launch {
            deleteReceiptUseCase(id).onSuccess {
                loadReceipts()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(error = error.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}