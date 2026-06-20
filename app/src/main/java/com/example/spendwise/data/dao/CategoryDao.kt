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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)
}