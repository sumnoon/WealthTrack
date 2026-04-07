package com.wealthtrack.domain

/**
 * Domain Use Case: Calculate investment suggestion using 50/30/20 rule
 *
 * The 50/30/20 rule divides income into:
 * - 50% for Needs (housing, groceries, utilities, etc.)
 * - 30% for Wants (entertainment, dining out, hobbies, etc.)
 * - 20% for Savings/Investments (retirement, emergency fund, investments)
 *
 * This use case focuses on the 20% savings/investments portion.
 */
class CalculateInvestmentSuggestionUseCase {

    data class BudgetBreakdown(
        val monthlyIncome: Double,
        val needsAmount: Double,
        val wantsAmount: Double,
        val savingsInvestmentsAmount: Double
    )

    /**
     * Calculates the 50/30/20 budget breakdown
     *
     * @param monthlyIncome Total monthly income
     * @return BudgetBreakdown with allocations for needs, wants, and savings/investments
     */
    operator fun invoke(monthlyIncome: Double): BudgetBreakdown {
        if (monthlyIncome < 0) {
            throw IllegalArgumentException("Income cannot be negative")
        }

        val needs = monthlyIncome * 0.50
        val wants = monthlyIncome * 0.30
        val savingsInvestments = monthlyIncome * 0.20

        return BudgetBreakdown(
            monthlyIncome = monthlyIncome,
            needsAmount = needs,
            wantsAmount = wants,
            savingsInvestmentsAmount = savingsInvestments
        )
    }

    /**
     * Formats currency value to string
     */
    fun formatCurrency(amount: Double): String {
        return String.format("$%.2f", amount)
    }
}
