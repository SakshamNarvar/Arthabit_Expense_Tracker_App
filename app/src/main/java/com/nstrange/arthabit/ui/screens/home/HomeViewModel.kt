package com.nstrange.arthabit.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstrange.arthabit.domain.model.Expense
import com.nstrange.arthabit.domain.repository.ExpenseRepository
import com.nstrange.arthabit.domain.repository.SmsRepository
import com.nstrange.arthabit.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val expenses: List<Expense> = emptyList(),
    val isLoadingExpenses: Boolean = false,
    val isAddingExpense: Boolean = false,
    val isUpdatingExpense: Boolean = false,
    val expenseError: String? = null,
    val showAddExpenseSheet: Boolean = false,
    val addExpenseSuccess: Boolean = false,
    val addExpenseError: String? = null,
    val expenseToEdit: Expense? = null,
    val updateExpenseError: String? = null,
    val updateExpenseSuccess: Boolean = false,
    val isSyncingSms: Boolean = false,
    val syncSmsError: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val smsRepository: SmsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchExpenses()
    }

    fun fetchExpenses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingExpenses = true, expenseError = null) }

            val result = expenseRepository.getExpenses()

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            expenses = result.data,
                            isLoadingExpenses = false,
                            expenseError = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoadingExpenses = false, expenseError = result.message)
                    }
                }
                is Resource.Loading -> { /* no-op */ }
            }
        }
    }

    fun showAddExpenseSheet() {
        _uiState.update { it.copy(showAddExpenseSheet = true, expenseToEdit = null, addExpenseError = null, updateExpenseError = null) }
    }

    fun showEditExpenseSheet(expense: Expense) {
        _uiState.update { it.copy(showAddExpenseSheet = true, expenseToEdit = expense, addExpenseError = null, updateExpenseError = null) }
    }

    fun hideAddExpenseSheet() {
        _uiState.update { it.copy(showAddExpenseSheet = false, expenseToEdit = null, addExpenseError = null, updateExpenseError = null) }
    }

    fun addExpense(
        amount: Double,
        merchant: String,
        currency: String,
        notes: String? = null,
        category: String? = null,
        fundSource: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingExpense = true, addExpenseError = null) }

            val result = expenseRepository.addExpense(
                amount,
                merchant,
                currency,
                notes,
                category,
                fundSource
            )

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isAddingExpense = false,
                            showAddExpenseSheet = false,
                            addExpenseSuccess = true
                        )
                    }
                    fetchExpenses()
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isAddingExpense = false,
                            addExpenseError = result.message
                        )
                    }
                }
                is Resource.Loading -> { /* no-op */ }
            }
        }
    }

    fun updateExpense(
        externalId: String,
        amount: Double?,
        merchant: String?,
        currency: String?,
        notes: String?,
        category: String?,
        fundSource: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingExpense = true, updateExpenseError = null) }

            val result = expenseRepository.updateExpense(
                externalId,
                amount,
                merchant,
                currency,
                notes,
                category,
                fundSource
            )

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isUpdatingExpense = false,
                            showAddExpenseSheet = false,
                            expenseToEdit = null,
                            updateExpenseSuccess = true
                        )
                    }
                    fetchExpenses()
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isUpdatingExpense = false,
                            updateExpenseError = result.message
                        )
                    }
                }
                is Resource.Loading -> { /* no-op */ }
            }
        }
    }

    fun syncSms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncingSms = true, syncSmsError = null) }
            val result = smsRepository.syncUnprocessedSms()
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSyncingSms = false, syncSmsError = null) }
                    // Fetch expenses again to refesh the list after syncing
                    fetchExpenses()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSyncingSms = false, syncSmsError = result.message) }
                }
                is Resource.Loading -> { /* no-op */ }
            }
        }
    }
}
