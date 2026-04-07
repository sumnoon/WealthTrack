package com.wealthtrack.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Budget Settings (singleton configuration)
 */
@Dao
interface BudgetSettingsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBudgetSettings(settings: BudgetSettingsEntity)

    @Update
    suspend fun updateBudgetSettings(settings: BudgetSettingsEntity)

    @Query("SELECT * FROM budget_settings WHERE id = 1")
    fun getBudgetSettings(): Flow<BudgetSettingsEntity?>

    @Query("SELECT * FROM budget_settings WHERE id = 1")
    suspend fun getBudgetSettingsOnce(): BudgetSettingsEntity?
}
