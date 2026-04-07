package com.wealthtrack.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthtrack.data.local.BudgetSettingsDao
import com.wealthtrack.data.local.BudgetSettingsEntity
import com.wealthtrack.data.local.ExpenseDao
import com.wealthtrack.data.local.ExpenseEntity
import com.wealthtrack.domain.AlertSeverity
import com.wealthtrack.domain.BudgetAlert
import com.wealthtrack.domain.GetBudgetStatusUseCase
import com.wealthtrack.domain.TrackExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BudgetTrackerViewModel @Inject constructor(
    private val trackExpenseUseCase: TrackExpenseUseCase,
    private val getBudgetStatusUseCase: GetBudgetStatusUseCase,
    private val expenseDao: ExpenseDao,
    private val budgetSettingsDao: BudgetSettingsDao
) : ViewModel() {

    var state by mutableStateOf(BudgetTrackerState())
        private set

    // Form state for adding expenses
    private var _expenseAmount by mutableStateOf("")
    private var _expenseCategory by mutableStateOf("")
    private var _expenseBudgetCategory by mutableStateOf("Needs")
    private var _expenseDescription by mutableStateOf("")
    private var _expenseDate by mutableStateOf(System.currentTimeMillis())

    // Alerts state
    private var _alerts by mutableStateOf<List<BudgetAlert>>(emptyList())

    init {
        loadBudgetStatus()
        loadAlerts()
        loadExpenses()
        loadBudgetSettings()
    }

    fun onExpenseAmountChanged(amount: String) {
        _expenseAmount = amount
        state = state.copy(expenseAmount = amount)
    }

    fun onExpenseCategoryChanged(category: String) {
        _expenseCategory = category
        state = state.copy(expenseCategory = category)
    }

    fun onExpenseBudgetCategoryChanged(category: String) {
        _expenseBudgetCategory = category
        state = state.copy(expenseBudgetCategory = category)
    }

    fun onExpenseDescriptionChanged(description: String) {
        _expenseDescription = description
        state = state.copy(expenseDescription = description)
    }

    fun onExpenseDateChanged(date: Long) {
        _expenseDate = date
        state = state.copy(expenseDate = date)
    }

    /**
     * Saves an expense to the database
     */
    fun saveExpense() {
        val amount = _expenseAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            state = state.copy(errorMessage = "Please enter a valid amount")
            return
        }

        if (_expenseCategory.isBlank()) {
            state = state.copy(errorMessage = "Please enter a category")
            return
        }

        viewModelScope.launch {
            try {
                trackExpenseUseCase.addExpense(
                    amount = amount,
                    category = _expenseCategory,
                    budgetCategory = _expenseBudgetCategory,
                    date = _expenseDate,
                    description = _expenseDescription
                )
                state = state.copy(
                    successMessage = "Expense saved successfully!",
                    expenseAmount = "",
                    expenseCategory = "",
                    expenseDescription = "",
                    errorMessage = null
                )
                _expenseAmount = ""
                _expenseCategory = ""
                _expenseDescription = ""
                loadExpenses()
                loadBudgetStatus()
            } catch (e: Exception) {
                state = state.copy(errorMessage = "Error saving expense: ${e.message}")
            }
        }
    }

    /**
     * Deletes an expense
     */
    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseDao.deleteExpense(expense)
            loadExpenses()
            loadBudgetStatus()
        }
    }

    /**
     * Saves budget settings
     */
    fun saveBudgetSettings(monthlyIncome: Double) {
        viewModelScope.launch {
            val settings = BudgetSettingsEntity(
                monthlyIncome = monthlyIncome,
                needsBudget = monthlyIncome * 0.50,
                wantsBudget = monthlyIncome * 0.30,
                savingsBudget = monthlyIncome * 0.20
            )
            budgetSettingsDao.insertBudgetSettings(settings)
            loadBudgetStatus()
        }
    }

    /**
     * Dismisses an alert
     */
    fun dismissAlert(category: String) {
        _alerts = _alerts.filter { it.category != category }
    }

    /**
     * Clears form for new entry
     */
    fun clearForm() {
        _expenseAmount = ""
        _expenseCategory = ""
        _expenseBudgetCategory = "Needs"
        _expenseDescription = ""
        _expenseDate = System.currentTimeMillis()
        state = state.copy(
            expenseAmount = "",
            expenseCategory = "",
            expenseBudgetCategory = "Needs",
            expenseDescription = "",
            expenseDate = System.currentTimeMillis(),
            errorMessage = null,
            successMessage = null
        )
    }

    fun clearMessages() {
        state = state.copy(errorMessage = null, successMessage = null)
    }

    private fun loadBudgetStatus() {
        viewModelScope.launch {
            getBudgetStatusUseCase.getCurrentMonthBudgetStatus()
                .catch { e ->
                    state = state.copy(errorMessage = "Error loading budget: ${e.message}")
                }
                .collect { statuses ->
                    state = state.copy(budgetStatuses = statuses)
                }
        }
    }

    private fun loadAlerts() {
        viewModelScope.launch {
            getBudgetStatusUseCase.getBudgetAlerts()
                .collect { alerts ->
                    _alerts = alerts
                    state = state.copy(alerts = alerts)
                }
        }
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            expenseDao.getAllExpenses()
                .catch { e ->
                    state = state.copy(errorMessage = "Error loading expenses: ${e.message}")
                }
                .collect { expenses ->
                    state = state.copy(expenses = expenses)
                }
        }
    }

    private fun loadBudgetSettings() {
        viewModelScope.launch {
            budgetSettingsDao.getBudgetSettings()
                .collect { settings ->
                    state = state.copy(budgetSettings = settings)
                }
        }
    }
}

data class BudgetTrackerState(
    val expenseAmount: String = "",
    val expenseCategory: String = "",
    val expenseBudgetCategory: String = "Needs",
    val expenseDescription: String = "",
    val expenseDate: Long = System.currentTimeMillis(),
    val expenses: List<ExpenseEntity> = emptyList(),
    val budgetStatuses: List<GetBudgetStatusUseCase.BudgetStatus> = emptyList(),
    val alerts: List<BudgetAlert> = emptyList(),
    val budgetSettings: BudgetSettingsEntity? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
