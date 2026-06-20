package com.example.spendwise.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.spendwise.data.models.Transaction
import com.example.spendwise.data.models.TransactionsType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionsType): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    suspend fun getTotalByType(type: TransactionsType): Double?

    @Query("""
        SELECT category, SUM(amount) as total
        FROM transactions
        WHERE type = :type AND date >= :startDate
        GROUP BY category
    """)
    fun getCategoryTotal(type: TransactionsType, startDate: Long = 0): Flow<Map<String, Double>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

}