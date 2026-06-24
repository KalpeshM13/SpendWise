package dev.kalpeshmore.spendwise.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.kalpeshmore.spendwise.data.database.AppDatabase
import dev.kalpeshmore.spendwise.data.models.Category
import dev.kalpeshmore.spendwise.data.models.Transaction
import dev.kalpeshmore.spendwise.data.models.TransactionsType
import dev.kalpeshmore.spendwise.data.repository.SpendwiseRepo
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

    fun getCategoriesByType(type: TransactionsType) = repository.getCategoriesByType(type)

    fun addTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.addTransaction(transaction)
    }

    fun addCategory(category: Category) = viewModelScope.launch {
        repository.addCategory(category)
    }
}