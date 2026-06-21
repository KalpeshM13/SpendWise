package com.example.spendwise.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendwise.data.database.AppDatabase
import com.example.spendwise.data.models.Category
import com.example.spendwise.data.models.Transaction
import com.example.spendwise.data.models.TransactionsType
import com.example.spendwise.data.repository.SpendwiseRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SpendwiseViewModel(application: Application): AndroidViewModel(application) {
    private val repository: SpendwiseRepo
    val allTransactions: Flow<List<Transaction>>
    val allCategories: Flow<List<Category>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SpendwiseRepo(database.transactionDao(), database.categoryDao())
        allTransactions = repository.allTransactions
        allCategories = repository.allCategories
    }

    fun getTransactionByType(type: TransactionsType) = repository.getTransactionByType(type)

    fun getCategoriesByType(type: TransactionsType) = repository.getCategoriesByType(type)

    fun getCategoryTotal(type: TransactionsType, startDate: Long = 0) = repository.getCategoryTotal(type, startDate)

    suspend fun getTotalByType(type: TransactionsType) = repository.getTotalByType(type)

    fun addTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.addTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
    }

    fun addCategory(category: Category) = viewModelScope.launch {
        repository.addCategory(category)
    }
}