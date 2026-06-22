package com.example.spendwise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.spendwise.data.dao.CategoryDao
import com.example.spendwise.data.dao.TransactionDao
import com.example.spendwise.data.models.Category
import com.example.spendwise.data.models.Transaction
import com.example.spendwise.data.models.TransactionsType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt
import androidx.room.TypeConverters

@Database(
    entities = [Transaction::class, Category::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TransactionTypeConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "SpendWise_database"
                ).apply {
                    addCallback(object: Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val categoryDao = database.categoryDao()

//                                    Default Expense Categories
                                    categoryDao.insert(Category("Food & Dining", TransactionsType.EXPENSE,"#FF5252".toColorInt()))
                                    categoryDao.insert(Category("Transportation", TransactionsType.EXPENSE,"#448AFF".toColorInt()))
                                    categoryDao.insert(Category("Shopping", TransactionsType.EXPENSE,"#FFC107".toColorInt()))
                                    categoryDao.insert(Category("Bills & Utilities", TransactionsType.EXPENSE,"#4CAF50".toColorInt()))
                                    categoryDao.insert(Category("Healthcare", TransactionsType.EXPENSE,"#E040FB".toColorInt()))

//                                    Default Income Categories
                                    categoryDao.insert(Category("Salary", TransactionsType.INCOME,"#00BC04".toColorInt()))
                                    categoryDao.insert(Category("Freelance", TransactionsType.INCOME,"#9C27B0".toColorInt()))
                                    categoryDao.insert(Category("Investments", TransactionsType.INCOME,"#4CAF50".toColorInt()))

                                }
                            }
                        }
                    })
                }.build()

                INSTANCE = instance
                instance
            }
        }
    }
}