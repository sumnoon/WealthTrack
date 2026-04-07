package com.wealthtrack.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthtrack.data.local.InvestmentDao
import com.wealthtrack.data.local.InvestmentEntity
import com.wealthtrack.domain.CalculateARRUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the ARR Calculator screen
 * Manages UI state and handles user interactions
 */
@HiltViewModel
class ARRViewModel @Inject constructor(
    private val calculateARRUseCase: CalculateARRUseCase,
    private val investmentDao: InvestmentDao
) : ViewModel() {

    // UI State
    var state by mutableStateOf(ARRState())
        private set

    // Form inputs
    private var beginningValue by mutableStateOf("")
    private var endingValue by mutableStateOf("")
    private var numberOfYears by mutableStateOf("")
    private var investmentName by mutableStateOf("")

    fun onBeginningValueChanged(value: String) {
        beginningValue = value
        state = state.copy(beginningValue = value)
    }

    fun onEndingValueChanged(value: String) {
        endingValue = value
        state = state.copy(endingValue = value)
    }

    fun onNumberOfYearsChanged(value: String) {
        numberOfYears = value
        state = state.copy(numberOfYears = value)
    }

    fun onNameChanged(value: String) {
        investmentName = value
        state = state.copy(investmentName = value)
    }

    /**
     * Calculates the ARR based on current form values
     */
    fun calculateARR() {
        val beginVal = beginningValue.toDoubleOrNull()
        val endVal = endingValue.toDoubleOrNull()
        val years = numberOfYears.toDoubleOrNull()

        if (beginVal == null || endVal == null || years == null) {
            state = state.copy(
                errorMessage = "Please enter valid numbers for all fields",
                calculatedARR = null
            )
            return
        }

        if (beginVal <= 0 || endVal <= 0 || years <= 0) {
            state = state.copy(
                errorMessage = "All values must be greater than zero",
                calculatedARR = null
            )
            return
        }

        val arr = calculateARRUseCase(beginVal, endVal, years)
        state = state.copy(
            calculatedARR = arr,
            formattedARR = arr?.let { calculateARRUseCase.formatAsPercentage(it) },
            errorMessage = null
        )
    }

    /**
     * Saves the investment to the encrypted database
     */
    fun saveInvestment() {
        val beginVal = beginningValue.toDoubleOrNull()
        val endVal = endingValue.toDoubleOrNull()
        val years = numberOfYears.toDoubleOrNull()

        if (beginVal == null || endVal == null || years == null) {
            state = state.copy(errorMessage = "Please enter valid numbers")
            return
        }

        val arr = state.calculatedARR

        viewModelScope.launch {
            val investment = InvestmentEntity(
                name = investmentName.ifBlank { "Untitled Investment" },
                beginningValue = beginVal,
                endingValue = endVal,
                numberOfYears = years,
                calculatedARR = arr
            )
            investmentDao.insertInvestment(investment)
            state = state.copy(
                successMessage = "Investment saved successfully!",
                errorMessage = null
            )
        }
    }

    /**
     * Clears the form for a new calculation
     */
    fun clearForm() {
        beginningValue = ""
        endingValue = ""
        numberOfYears = ""
        investmentName = ""
        state = ARRState()
    }

    fun clearMessages() {
        state = state.copy(errorMessage = null, successMessage = null)
    }
}

/**
 * Immutable UI state for the ARR Calculator
 */
data class ARRState(
    val investmentName: String = "",
    val beginningValue: String = "",
    val endingValue: String = "",
    val numberOfYears: String = "",
    val calculatedARR: Double? = null,
    val formattedARR: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
