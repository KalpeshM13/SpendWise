package com.example.spendwise.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spendwise.data.models.Category
import com.example.spendwise.data.models.TransactionsType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type")
    fun getCategoryByType(type: TransactionsType): Flow<List<Category>>

    @Query("""
        SELECT category, SUM(amount) as total
        FROM transactions
        WHERE type = :type AND date >= :startDate
        GROUP BY category
    """)
    fun getCategoryTotal(type: TransactionsType, startDate: Long = 0): Flow<Map<String, Double>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)
}