package com.wealthtrack.data

import android.content.Context
import com.wealthtrack.data.local.BudgetSettingsDao
import com.wealthtrack.data.local.ExpenseDao
import com.wealthtrack.data.local.InvestmentDao
import com.wealthtrack.data.local.WealthTrackDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WealthTrackDatabase {
        return WealthTrackDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideInvestmentDao(database: WealthTrackDatabase): InvestmentDao {
        return database.investmentDao()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: WealthTrackDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    @Singleton
    fun provideBudgetSettingsDao(database: WealthTrackDatabase): BudgetSettingsDao {
        return database.budgetSettingsDao()
    }
}
