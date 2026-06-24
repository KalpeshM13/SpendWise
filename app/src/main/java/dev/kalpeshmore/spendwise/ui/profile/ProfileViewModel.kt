package dev.kalpeshmore.spendwise.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.kalpeshmore.spendwise.data.database.AppDatabase
import dev.kalpeshmore.spendwise.data.models.CategoryTotal
import dev.kalpeshmore.spendwise.data.models.DashboardState
import dev.kalpeshmore.spendwise.data.models.ExpenseCategoryData
import dev.kalpeshmore.spendwise.data.models.TransactionsType
import dev.kalpeshmore.spendwise.data.repository.SpendwiseRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SpendwiseRepo = SpendwiseRepo(
        AppDatabase.getDatabase(application).transactionDao(),
        AppDatabase.getDatabase(application).categoryDao()
    )

    // Holds the currently selected month and year
    private val selectedDateFlow = MutableStateFlow(
        Pair(Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR))
    )

    fun setMonthAndYear(month: Int, year: Int) {
        selectedDateFlow.value = Pair(month, year)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val dashboardState: StateFlow<DashboardState> = selectedDateFlow.flatMapLatest { (month, year) ->
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = calendar.timeInMillis

        combine(
            repository.getTotalByTypeAndDateRange(TransactionsType.INCOME, startDate, endDate),
            repository.getTotalByTypeAndDateRange(TransactionsType.EXPENSE, startDate, endDate),
            repository.getCategoryTotalByDateRange(TransactionsType.EXPENSE, startDate, endDate)
        ) { income: Double, expense: Double, categoryTotals: List<CategoryTotal> ->
            DashboardState(
                totalIncome = income,
                totalExpense = expense,
                balance = income - expense,
                expenseCategories = categoryTotals.map { categoryTotal ->
                    ExpenseCategoryData(
                        category = categoryTotal.categoryName,
                        amount = categoryTotal.totalAmount
                    )
                }
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardState()
    )
}