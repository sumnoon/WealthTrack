package com.wealthtrack.domain

import com.wealthtrack.data.local.BudgetSettingsDao
import com.wealthtrack.data.local.ExpenseDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Domain Use Case: Calculate current budget status
 * Combines budget settings with actual expenses to show remaining budget
 */
class GetBudgetStatusUseCase @Inject constructor(
    private val budgetSettingsDao: BudgetSettingsDao,
    private val expenseDao: ExpenseDao
) {
    data class BudgetStatus(
        val budgetCategory: String, // Needs, Wants, or Savings
        val budgetedAmount: Double,
        val spentAmount: Double,
        val remainingAmount: Double,
        val percentageUsed: Double,
        val status: BudgetStatusType
    )

    enum class BudgetStatusType {
        ON_TRACK,       // 0-79% used
        WARNING,        // 80-94% used
        CRITICAL,       // 95-100% used
        EXCEEDED        // Over 100%
    }

    /**
     * Gets budget status for all three categories for current month
     */
    fun getCurrentMonthBudgetStatus(): Flow<List<BudgetStatus>> {
        val now = System.currentTimeMillis()
        val startOfMonth = getStartOfMonth(now)
        val endOfMonth = getEndOfMonth(now)

        return budgetSettingsDao.getBudgetSettings()
            .combine(expenseDao.getTotalByBudgetCategoryAndDateRange("Needs", startOfMonth, endOfMonth)) { settings, needsSpent ->
                Triple(settings, needsSpent ?: 0.0, null)
            }
            .combine(expenseDao.getTotalByBudgetCategoryAndDateRange("Wants", startOfMonth, endOfMonth)) { acc, wantsSpent ->
                Triple(acc.first, acc.second, wantsSpent)
            }
            .combine(expenseDao.getTotalByBudgetCategoryAndDateRange("Savings", startOfMonth, endOfMonth)) { acc, savingsSpent ->
                val settings = acc.first
                val needsSpent = acc.second
                val wantsSpent = acc.third ?: 0.0
                val savingsSpentAmount = savingsSpent ?: 0.0

                if (settings == null) {
                    emptyList()
                } else {
                    listOf(
                        BudgetStatus(
                            budgetCategory = "Needs",
                            budgetedAmount = settings.needsBudget,
                            spentAmount = needsSpent,
                            remainingAmount = settings.needsBudget - needsSpent,
                            percentageUsed = if (settings.needsBudget > 0) (needsSpent / settings.needsBudget * 100) else 0.0,
                            status = getStatus(needsSpent, settings.needsBudget, settings.warningThreshold, settings.criticalThreshold)
                        ),
                        BudgetStatus(
                            budgetCategory = "Wants",
                            budgetedAmount = settings.wantsBudget,
                            spentAmount = wantsSpent,
                            remainingAmount = settings.wantsBudget - wantsSpent,
                            percentageUsed = if (settings.wantsBudget > 0) (wantsSpent / settings.wantsBudget * 100) else 0.0,
                            status = getStatus(wantsSpent, settings.wantsBudget, settings.warningThreshold, settings.criticalThreshold)
                        ),
                        BudgetStatus(
                            budgetCategory = "Savings",
                            budgetedAmount = settings.savingsBudget,
                            spentAmount = savingsSpentAmount,
                            remainingAmount = settings.savingsBudget - savingsSpentAmount,
                            percentageUsed = if (settings.savingsBudget > 0) (savingsSpentAmount / settings.savingsBudget * 100) else 0.0,
                            status = getStatus(savingsSpentAmount, settings.savingsBudget, settings.warningThreshold, settings.criticalThreshold)
                        )
                    )
                }
            }
    }

    /**
     * Checks if alerts should be triggered and returns list of alerts
     */
    fun getBudgetAlerts(): Flow<List<BudgetAlert>> {
        return getCurrentMonthBudgetStatus()
            .combine(budgetSettingsDao.getBudgetSettings()) { statuses, settings ->
                if (settings == null || !settings.alertsEnabled) {
                    emptyList()
                } else {
                    statuses.mapNotNull { status ->
                        when (status.status) {
                            BudgetStatusType.WARNING -> BudgetAlert(
                                category = status.budgetCategory,
                                message = "Warning: You've used ${String.format("%.0f", status.percentageUsed)}% of your ${status.budgetCategory} budget",
                                percentageUsed = status.percentageUsed,
                                severity = AlertSeverity.WARNING
                            )
                            BudgetStatusType.CRITICAL -> BudgetAlert(
                                category = status.budgetCategory,
                                message = "Critical: You've used ${String.format("%.0f", status.percentageUsed)}% of your ${status.budgetCategory} budget",
                                percentageUsed = status.percentageUsed,
                                severity = AlertSeverity.CRITICAL
                            )
                            BudgetStatusType.EXCEEDED -> BudgetAlert(
                                category = status.budgetCategory,
                                message = "Alert: You've exceeded your ${status.budgetCategory} budget!",
                                percentageUsed = status.percentageUsed,
                                severity = AlertSeverity.EXCEEDED
                            )
                            else -> null
                        }
                    }
                }
            }
    }

    private fun getStatus(
        spent: Double,
        budgeted: Double,
        warningThreshold: Double,
        criticalThreshold: Double
    ): BudgetStatusType {
        val percentage = if (budgeted > 0) (spent / budgeted * 100) else 0.0
        return when {
            percentage >= 100 -> BudgetStatusType.EXCEEDED
            percentage >= criticalThreshold -> BudgetStatusType.CRITICAL
            percentage >= warningThreshold -> BudgetStatusType.WARNING
            else -> BudgetStatusType.ON_TRACK
        }
    }

    private fun getStartOfMonth(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfMonth(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}

data class BudgetAlert(
    val category: String,
    val message: String,
    val percentageUsed: Double,
    val severity: AlertSeverity
)

enum class AlertSeverity {
    WARNING, CRITICAL, EXCEEDED
}
