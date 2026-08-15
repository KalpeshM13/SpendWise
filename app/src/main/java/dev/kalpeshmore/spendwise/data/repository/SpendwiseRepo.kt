package dev.kalpeshmore.spendwise.data.repository

import dev.kalpeshmore.spendwise.data.dao.CategoryDao
import dev.kalpeshmore.spendwise.data.dao.TransactionDao
import dev.kalpeshmore.spendwise.data.firebase.FirebaseService
import dev.kalpeshmore.spendwise.data.models.Category
import dev.kalpeshmore.spendwise.data.models.Transaction
import dev.kalpeshmore.spendwise.data.models.TransactionsType
import dev.kalpeshmore.spendwise.data.models.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SpendwiseRepo(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val firebaseService: FirebaseService = FirebaseService()
) {
    val allTransactions = transactionDao.getAllTransactions()
    val allCategories = categoryDao.getAllCategories()

    fun getTransactionByType(type: TransactionsType) = transactionDao.getTransactionsByType(type)
    fun getCategoriesByType(type: TransactionsType) = categoryDao.getCategoriesByType(type)
    fun getCategoryTotal(type: TransactionsType, startDate: Long = 0) = transactionDao.getCategoryTotal(type, startDate)
    fun getCategoryTotalByDateRange(type: TransactionsType, startDate: Long, endDate: Long) = transactionDao.getCategoryTotalByDateRange(type, startDate, endDate)

    fun getTotalByType(type: TransactionsType) = transactionDao.getTotalByType(type)
    fun getTotalByTypeAndDateRange(type: TransactionsType, startDate: Long, endDate: Long) = transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate)

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
        try { firebaseService.addTransaction(transaction) } catch (_: Exception) {}
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
        try { firebaseService.deleteTransaction(transaction) } catch (_: Exception) {}
    }

    suspend fun addCategory(category: Category) {
        categoryDao.insert(category)
        try { firebaseService.addCategory(category) } catch (_: Exception) {}
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
        try { firebaseService.deleteCategory(category) } catch (_: Exception) {}
    }

    // ─── Cloud Sync ─────────────────────────────────────────────────

    /**
     * Upload all local data to Firestore.
     * Called after first login to ensure local data is persisted to cloud.
     */
    suspend fun syncLocalToCloud() {
        try {
            val localTransactions = allTransactions.first()
            if (localTransactions.isNotEmpty()) {
                firebaseService.syncTransactionsToFirestore(localTransactions)
            }
            val localCategories = allCategories.first()
            if (localCategories.isNotEmpty()) {
                firebaseService.syncCategoriesToFirestore(localCategories)
            }
        } catch (_: Exception) {}
    }

    /**
     * Pull all data from Firestore and insert into local Room DB.
     * Cloud wins: clears local first, then inserts cloud data.
     */
    suspend fun syncFromCloud() {
        try {
            val cloudTransactions = firebaseService.fetchAllTransactions()
            if (cloudTransactions.isNotEmpty()) {
                transactionDao.deleteAll()
                for (t in cloudTransactions) {
                    transactionDao.insert(t)
                }
            }
            
            val cloudCategories = firebaseService.fetchAllCategories()
            if (cloudCategories.isNotEmpty()) {
                categoryDao.deleteAll()
                for (c in cloudCategories) {
                    categoryDao.insert(c)
                }
            }
        } catch (_: Exception) {}
    }

    // ─── Profile ────────────────────────────────────────────────────

    suspend fun saveUserProfile(profile: UserProfile) {
        try { firebaseService.saveUserProfile(profile) } catch (_: Exception) {}
    }

    suspend fun getUserProfile(): UserProfile? {
        return try { firebaseService.getUserProfile() } catch (_: Exception) { null }
    }

    fun observeUserProfile(): Flow<UserProfile?> = firebaseService.observeUserProfile()
}