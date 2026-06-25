package dev.kalpeshmore.spendwise.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.kalpeshmore.spendwise.data.models.Category
import dev.kalpeshmore.spendwise.data.models.CategoryTotal
import dev.kalpeshmore.spendwise.data.models.TransactionsType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type")
    fun getCategoriesByType(type: TransactionsType): Flow<List<Category>>

    @Query("""
        SELECT c.name as categoryName, COALESCE(SUM(t.amount), 0.0) as totalAmount
        FROM categories c
        LEFT JOIN transactions t ON c.name = t.category
        WHERE c.type = :type AND (t.date >= :startDate OR t.date IS NULL)
        GROUP BY c.name
    """)
    fun getCategoryTotal(type: TransactionsType, startDate: Long = 0): Flow<List<CategoryTotal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @androidx.room.Delete
    suspend fun delete(category: Category)
}