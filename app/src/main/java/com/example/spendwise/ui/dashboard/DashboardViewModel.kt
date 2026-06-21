package com.example.spendwise.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SpendwiseRepo = SpendwiseRepo(
        AppDatabase.getDatabase(application).transactionDao(),
        AppDatabase.getDatabase(application).categoryDao()
    )

    val dashboardState: StateFlow<DashboardState> = combine(
        repository.getTotalByType(TransactionsType.INCOME),
        repository.getTotalByType(TransactionsType.EXPENSE),
        repository.getCategoryTotal(TransactionsType.EXPENSE)
    ) { income: Double, expense: Double, categoryTotals: List<CategoryTotal> ->
        DashboardState(
            totalIncome = income,
            totalExpense = expense,
            balance = income-expense,
            expenseCategories = categoryTotals.map { categoryTotal ->
                ExpenseCategoryData(
                    category = categoryTotal.categoryName,
                    amount = categoryTotal.totalAmount
                )
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardState()
    )
}