package com.wealthtrack.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Investment entities
 * Provides type-safe database operations via Room
 */
@Dao
interface InvestmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: InvestmentEntity)

    @Delete
    suspend fun deleteInvestment(investment: InvestmentEntity)

    @Update
    suspend fun updateInvestment(investment: InvestmentEntity)

    @Query("SELECT * FROM investments ORDER BY dateCreated DESC")
    fun getAllInvestments(): Flow<List<InvestmentEntity>>

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getInvestmentById(id: String): InvestmentEntity?
}
