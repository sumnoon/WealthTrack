package com.wealthtrack.data.local

import androidx.room.*

/**
 * Budget settings entity for storing user's monthly income and calculated budget
 * Allows persistence of budget configuration
 */
@Entity(
    tableName = "budget_settings",
    indices = [Index(value = ["id"], unique = true)]
)
data class BudgetSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Singleton - only one record
    val monthlyIncome: Double = 0.0,
    val needsBudget: Double = 0.0, // 50%
    val wantsBudget: Double = 0.0, // 30%
    val savingsBudget: Double = 0.0, // 20%
    val lastUpdated: Long = System.currentTimeMillis(),
    // Alert thresholds (percentage of budget)
    val warningThreshold: Double = 80.0, // Alert at 80%
    val criticalThreshold: Double = 95.0, // Alert at 95%
    val alertsEnabled: Boolean = true
)
