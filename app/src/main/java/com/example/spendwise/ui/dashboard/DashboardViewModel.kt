package com.example.spendwise.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendwise.data.database.AppDatabase
import com.example.spendwise.data.models.CategoryTotal
import com.example.spendwise.data.models.DashboardState
import com.example.spendwise.data.models.ExpenseCategoryData
import com.example.spendwise.data.models.TransactionsType
import com.example.spendwise.data.repository.SpendwiseRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlin.math.exp

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SpendwiseRepo = SpendwiseRepo(
        AppDatabase.getDatabase(application).transactionDao(),
        AppDatabase.getDatabase(application).categoryDao()
    )

    val dashboardState: StateFlow<DashboardState> = combine(
        flow { emit(repository.getTotalByType(TransactionsType.INCOME)) },
        flow { emit(repository.getTotalByType(TransactionsType.EXPENSE)) },
        repository.getCategoryTotal(TransactionsType.EXPENSE)
    ) { income: Double, expense: Double, categoryTotals: List<CategoryTotal> ->
        DashboardState(
            totalIncome = income,
            totalExpense = expense,
            balance = income-expense,
            expenseCategories = categoryTotals.map { categoryTotal ->
                ExpenseCategoryData(
                    category = categoryTotal.name,
                    amount = categoryTotal.total
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardState()
    )
}