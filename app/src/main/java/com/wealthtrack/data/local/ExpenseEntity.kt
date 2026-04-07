package com.wealthtrack.data.local

import androidx.room.*
import java.util.UUID

/**
 * Expense entity representing a single expense transaction
 * Stored in encrypted Room database via SQLCipher
 * Supports manual entry with custom categories
 */
@Entity(
    tableName = "expenses",
    indices = [
        Index(value = ["date"]),
        Index(value = ["category"]),
        Index(value = ["budgetCategory"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: String, // e.g., "Groceries", "Rent", "Dining Out"
    val budgetCategory: String, // "Needs", "Wants", or "Savings"
    val date: Long, // Timestamp in milliseconds
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
