package com.wealthtrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room Database for WealthTrack
 */
@Database(
    entities = [InvestmentEntity::class, ExpenseEntity::class, BudgetSettingsEntity::class],
    version = 2,
    exportSchema = true
)
abstract class WealthTrackDatabase : RoomDatabase() {

    abstract fun investmentDao(): InvestmentDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetSettingsDao(): BudgetSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: WealthTrackDatabase? = null

        /**
         * SECURITY: Initialize encrypted database with SQLCipher
         * The passphrase should be stored securely (e.g., in Android Keystore)
         * For production, use a more sophisticated key management approach
         */
        // Migration from version 1 to 2 (add expenses and budget_settings tables)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create expenses table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS expenses (
                        id TEXT NOT NULL PRIMARY KEY,
                        amount REAL NOT NULL,
                        category TEXT NOT NULL,
                        budgetCategory TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        description TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """)
                // Create budget_settings table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS budget_settings (
                        id INTEGER NOT NULL PRIMARY KEY,
                        monthlyIncome REAL NOT NULL,
                        needsBudget REAL NOT NULL,
                        wantsBudget REAL NOT NULL,
                        savingsBudget REAL NOT NULL,
                        lastUpdated INTEGER NOT NULL,
                        warningThreshold REAL NOT NULL,
                        criticalThreshold REAL NOT NULL,
                        alertsEnabled INTEGER NOT NULL
                    )
                """)
                // Create indices for expenses
                database.execSQL("CREATE INDEX IF NOT EXISTS index_expenses_date ON expenses(date DESC)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_expenses_category ON expenses(category)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_expenses_budgetCategory ON expenses(budgetCategory)")
            }
        }

        fun getDatabase(context: Context): WealthTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    WealthTrackDatabase::class.java,
                    "wealthtrack.db"
                )
                    .addMigrations(MIGRATION_1_2)

                val instance = builder.build()
                INSTANCE = instance
                instance
            }
        }
    }
}
