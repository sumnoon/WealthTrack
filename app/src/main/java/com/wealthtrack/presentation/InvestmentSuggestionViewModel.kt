package com.wealthtrack.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wealthtrack.domain.CalculateInvestmentSuggestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Investment Suggestion screen
 * Manages UI state for 50/30/20 budget breakdown
 */
@HiltViewModel
class InvestmentSuggestionViewModel @Inject constructor(
    private val calculateInvestmentSuggestionUseCase: CalculateInvestmentSuggestionUseCase
) : ViewModel() {

    var state by mutableStateOf(InvestmentSuggestionState())
        private set

    private var monthlyIncome by mutableStateOf("")

    fun onMonthlyIncomeChanged(value: String) {
        monthlyIncome = value
        state = state.copy(monthlyIncome = value)
    }

    /**
     * Calculates the 50/30/20 budget breakdown
     */
    fun calculateBudget() {
        val income = monthlyIncome.toDoubleOrNull()

        if (income == null || income < 0) {
            state = state.copy(
                errorMessage = "Please enter a valid income amount",
                budgetBreakdown = null
            )
            return
        }

        val breakdown = calculateInvestmentSuggestionUseCase(income)
        state = state.copy(
            budgetBreakdown = breakdown,
            errorMessage = null
        )
    }

    /**
     * Clears the form
     */
    fun clear() {
        monthlyIncome = ""
        state = InvestmentSuggestionState()
    }

    fun clearError() {
        state = state.copy(errorMessage = null)
    }
}

/**
 * Immutable UI state for the Investment Suggestion screen
 */
data class InvestmentSuggestionState(
    val monthlyIncome: String = "",
    val budgetBreakdown: CalculateInvestmentSuggestionUseCase.BudgetBreakdown? = null,
    val errorMessage: String? = null
)
