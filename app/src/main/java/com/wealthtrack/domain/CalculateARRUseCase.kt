package com.wealthtrack.domain

/**
 * Domain Use Case: Calculate Annualized Rate of Return (ARR)
 *
 * Formula: ARR = ((Ending Value / Beginning Value) ^ (1 / Number of Years)) - 1
 *
 * This encapsulates the core business logic for investment return calculations
 * and lives in the Domain layer of Clean Architecture.
 */
class CalculateARRUseCase {

    /**
     * Calculates the Annualized Rate of Return
     *
     * @param beginningValue Initial investment value
     * @param endingValue Final investment value
     * @param numberOfYears Investment period in years
     * @return ARR as a decimal (e.g., 0.15 = 15%), or null if calculation is invalid
     */
    operator fun invoke(
        beginningValue: Double,
        endingValue: Double,
        numberOfYears: Double
    ): Double? {
        // Validate inputs
        if (beginningValue <= 0 || endingValue <= 0 || numberOfYears <= 0) {
            return null
        }

        return try {
            val ratio = endingValue / beginningValue
            val exponent = 1.0 / numberOfYears
            Math.pow(ratio, exponent) - 1
        } catch (e: Exception) {
            // Handle edge cases like division by zero, overflow, etc.
            null
        }
    }

    /**
     * Converts decimal ARR to percentage string
     */
    fun formatAsPercentage(arr: Double): String {
        return String.format("%.2f%%", arr * 100)
    }
}
