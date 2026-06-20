package com.example.spendwise.data.repository

import com.example.spendwise.data.dao.CategoryDao
import com.example.spendwise.data.dao.TransactionDao
import com.example.spendwise.data.models.Category
import com.example.spendwise.data.models.Transaction
import com.example.spendwise.data.models.TransactionsType

class SpendwiseRepo(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    val allTransactions = transactionDao.getAllTransactions()
    val allCategories = categoryDao.getAllCategories()

    fun getTransactionByType(type: TransactionsType) = transactionDao.getTransactionsByType(type)
    fun getCategoriesByType(type: TransactionsType) = categoryDao.getCategoryByType(type)
    fun getCategoryTotal(type: TransactionsType, startDate: Long = 0) = transactionDao.getCategoryTotal(type, startDate)

    suspend fun getTotalByType(type: TransactionsType) = transactionDao.getTotalByType(type) ?: 0.0

    suspend fun addTransaction(transaction: Transaction) = transactionDao.insert(transaction)

    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)

    suspend fun addCategory(category: Category) = categoryDao.insert(category)

}