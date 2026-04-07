package com.wealthtrack.domain

import com.wealthtrack.data.local.BudgetSettingsDao
import com.wealthtrack.data.local.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide domain layer dependencies (Use Cases)
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideCalculateARRUseCase(): CalculateARRUseCase {
        return CalculateARRUseCase()
    }

    @Provides
    @Singleton
    fun provideCalculateInvestmentSuggestionUseCase(): CalculateInvestmentSuggestionUseCase {
        return CalculateInvestmentSuggestionUseCase()
    }

    @Provides
    @Singleton
    fun provideTrackExpenseUseCase(expenseDao: ExpenseDao): TrackExpenseUseCase {
        return TrackExpenseUseCase(expenseDao)
    }

    @Provides
    @Singleton
    fun provideGetBudgetStatusUseCase(
        budgetSettingsDao: BudgetSettingsDao,
        expenseDao: ExpenseDao
    ): GetBudgetStatusUseCase {
        return GetBudgetStatusUseCase(budgetSettingsDao, expenseDao)
    }
}
