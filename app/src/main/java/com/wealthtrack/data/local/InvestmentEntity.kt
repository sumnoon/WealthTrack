package com.wealthtrack.data.local

import androidx.room.*
import java.util.UUID

/**
 * Investment entity representing a single investment record in the database
 * Stored in encrypted Room database via SQLCipher
 */
@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val beginningValue: Double,
    val endingValue: Double,
    val numberOfYears: Double,
    val calculatedARR: Double?,
    val dateCreated: Long = System.currentTimeMillis(),
    val notes: String = ""
)
