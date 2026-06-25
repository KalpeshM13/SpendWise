package dev.kalpeshmore.spendwise.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.kalpeshmore.spendwise.data.database.AppDatabase
import dev.kalpeshmore.spendwise.data.models.Category
import dev.kalpeshmore.spendwise.data.repository.SpendwiseRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageCategoriesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SpendwiseRepo = SpendwiseRepo(
        AppDatabase.getDatabase(application).transactionDao(),
        AppDatabase.getDatabase(application).categoryDao()
    )

    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}
