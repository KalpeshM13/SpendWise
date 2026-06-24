package dev.kalpeshmore.spendwise.ui.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.kalpeshmore.spendwise.data.database.AppDatabase
import dev.kalpeshmore.spendwise.data.models.Transaction
import dev.kalpeshmore.spendwise.data.models.TransactionFilter
import dev.kalpeshmore.spendwise.data.models.TransactionsType
import dev.kalpeshmore.spendwise.data.repository.SpendwiseRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SpendwiseRepo = SpendwiseRepo(
        AppDatabase.getDatabase(application).transactionDao(),
        AppDatabase.getDatabase(application).categoryDao()
    )

    private val _transactionFilter = MutableStateFlow(TransactionFilter.ALL)

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = _transactionFilter.flatMapLatest { filter ->
        when(filter) {
            TransactionFilter.ALL -> repository.allTransactions
            TransactionFilter.INCOME -> repository.getTransactionByType(TransactionsType.INCOME)
            TransactionFilter.EXPENSE -> repository.getTransactionByType(TransactionsType.EXPENSE)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTransactionFilter(filter: TransactionFilter) {
        _transactionFilter.value = filter
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
        }
    }
}