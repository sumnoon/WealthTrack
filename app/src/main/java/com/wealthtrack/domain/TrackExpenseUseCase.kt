package com.wealthtrack.domain

import com.wealthtrack.data.local.ExpenseDao
import com.wealthtrack.data.local.ExpenseEntity
import javax.inject.Inject

/**
 * Domain Use Case: Add/Edit/Delete expenses
 * Encapsulates expense management logic
 */
class TrackExpenseUseCase @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    /**
     * Adds a new expense to the database
     */
    suspend fun addExpense(
        amount: Double,
        category: String,
        budgetCategory: String,
        date: Long,
        description: String = ""
    ): ExpenseEntity {
        require(amount > 0) { "Amount must be greater than zero" }
        require(category.isNotBlank()) { "Category cannot be blank" }
        require(budgetCategory in listOf("Needs", "Wants", "Savings")) {
            "Budget category must be Needs, Wants, or Savings"
        }

        val expense = ExpenseEntity(
            amount = amount,
            category = category,
            budgetCategory = budgetCategory,
            date = date,
            description = description
        )
        expenseDao.insertExpense(expense)
        return expense
    }

    /**
     * Updates an existing expense
     */
    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    /**
     * Deletes an expense
     */
    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    /**
     * Gets an expense by ID
     */
    suspend fun getExpenseById(id: String): ExpenseEntity? {
        return expenseDao.getExpenseById(id)
    }
}
