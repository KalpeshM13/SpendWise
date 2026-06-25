package dev.kalpeshmore.spendwise.data.repository

import dev.kalpeshmore.spendwise.data.dao.CategoryDao
import dev.kalpeshmore.spendwise.data.dao.TransactionDao
import dev.kalpeshmore.spendwise.data.models.Category
import dev.kalpeshmore.spendwise.data.models.Transaction
import dev.kalpeshmore.spendwise.data.models.TransactionsType

class SpendwiseRepo(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    val allTransactions = transactionDao.getAllTransactions()
    val allCategories = categoryDao.getAllCategories()

    fun getTransactionByType(type: TransactionsType) = transactionDao.getTransactionsByType(type)
    fun getCategoriesByType(type: TransactionsType) = categoryDao.getCategoriesByType(type)
    fun getCategoryTotal(type: TransactionsType, startDate: Long = 0) = transactionDao.getCategoryTotal(type, startDate)
    fun getCategoryTotalByDateRange(type: TransactionsType, startDate: Long, endDate: Long) = transactionDao.getCategoryTotalByDateRange(type, startDate, endDate)

    fun getTotalByType(type: TransactionsType) = transactionDao.getTotalByType(type)
    fun getTotalByTypeAndDateRange(type: TransactionsType, startDate: Long, endDate: Long) = transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate)

    suspend fun addTransaction(transaction: Transaction) = transactionDao.insert(transaction)

    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)

    suspend fun addCategory(category: Category) = categoryDao.insert(category)

    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)

}