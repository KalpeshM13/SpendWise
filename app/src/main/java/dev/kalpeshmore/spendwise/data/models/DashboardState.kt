package dev.kalpeshmore.spendwise.data.models

data class DashboardState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val expenseCategories: List<ExpenseCategoryData> = emptyList()
)
