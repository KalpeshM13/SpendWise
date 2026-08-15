package dev.kalpeshmore.spendwise.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dev.kalpeshmore.spendwise.data.models.Category
import dev.kalpeshmore.spendwise.data.models.Transaction
import dev.kalpeshmore.spendwise.data.models.TransactionsType
import dev.kalpeshmore.spendwise.data.models.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid

    private fun userDoc() = getUserId()?.let { db.collection("users").document(it) }

    // ─── Transactions ───────────────────────────────────────────────

    suspend fun addTransaction(transaction: Transaction) {
        val doc = userDoc() ?: return
        val data = mapOf(
            "id" to transaction.id,
            "amount" to transaction.amount,
            "description" to transaction.description,
            "category" to transaction.category,
            "type" to transaction.type.name,
            "date" to transaction.date
        )
        doc.collection("transactions")
            .document(transaction.id.toString())
            .set(data)
            .await()
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        val doc = userDoc() ?: return
        doc.collection("transactions")
            .document(transaction.id.toString())
            .delete()
            .await()
    }

    suspend fun fetchAllTransactions(): List<Transaction> {
        val doc = userDoc() ?: return emptyList()
        val snapshot = doc.collection("transactions").get().await()
        return snapshot.documents.mapNotNull { d ->
            try {
                Transaction(
                    id = (d.getLong("id") ?: 0).toInt(),
                    amount = d.getDouble("amount") ?: 0.0,
                    description = d.getString("description") ?: "",
                    category = d.getString("category") ?: "",
                    type = TransactionsType.valueOf(d.getString("type") ?: "EXPENSE"),
                    date = d.getLong("date") ?: System.currentTimeMillis()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun syncTransactionsToFirestore(transactions: List<Transaction>) {
        val doc = userDoc() ?: return
        val batch = db.batch()
        for (t in transactions) {
            val ref = doc.collection("transactions").document(t.id.toString())
            val data = mapOf(
                "id" to t.id,
                "amount" to t.amount,
                "description" to t.description,
                "category" to t.category,
                "type" to t.type.name,
                "date" to t.date
            )
            batch.set(ref, data)
        }
        batch.commit().await()
    }

    // ─── Categories ─────────────────────────────────────────────────

    suspend fun addCategory(category: Category) {
        val doc = userDoc() ?: return
        val data = mapOf(
            "name" to category.name,
            "type" to category.type.name,
            "color" to category.color
        )
        doc.collection("categories")
            .document(category.name)
            .set(data)
            .await()
    }

    suspend fun deleteCategory(category: Category) {
        val doc = userDoc() ?: return
        doc.collection("categories")
            .document(category.name)
            .delete()
            .await()
    }

    suspend fun fetchAllCategories(): List<Category> {
        val doc = userDoc() ?: return emptyList()
        val snapshot = doc.collection("categories").get().await()
        return snapshot.documents.mapNotNull { d ->
            try {
                Category(
                    name = d.getString("name") ?: return@mapNotNull null,
                    type = TransactionsType.valueOf(d.getString("type") ?: "EXPENSE"),
                    color = (d.getLong("color") ?: 0).toInt()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun syncCategoriesToFirestore(categories: List<Category>) {
        val doc = userDoc() ?: return
        val batch = db.batch()
        for (c in categories) {
            val ref = doc.collection("categories").document(c.name)
            val data = mapOf(
                "name" to c.name,
                "type" to c.type.name,
                "color" to c.color
            )
            batch.set(ref, data)
        }
        batch.commit().await()
    }

    // ─── User Profile ───────────────────────────────────────────────

    suspend fun saveUserProfile(profile: UserProfile) {
        val doc = userDoc() ?: return
        val data = mapOf(
            "name" to profile.name,
            "email" to profile.email,
            "phone" to profile.phone,
            "avatar" to profile.avatar
        )
        doc.collection("profile")
            .document("info")
            .set(data, SetOptions.merge())
            .await()
    }

    suspend fun getUserProfile(): UserProfile? {
        val doc = userDoc() ?: return null
        val snapshot = doc.collection("profile").document("info").get().await()
        return if (snapshot.exists()) {
            UserProfile(
                name = snapshot.getString("name") ?: "",
                email = snapshot.getString("email") ?: "",
                phone = snapshot.getString("phone") ?: "",
                avatar = snapshot.getString("avatar") ?: "girl"
            )
        } else null
    }

    fun observeUserProfile(): Flow<UserProfile?> = callbackFlow {
        val doc = userDoc()
        if (doc == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val registration = doc.collection("profile").document("info")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val profile = UserProfile(
                    name = snapshot.getString("name") ?: "",
                    email = snapshot.getString("email") ?: "",
                    phone = snapshot.getString("phone") ?: "",
                    avatar = snapshot.getString("avatar") ?: "girl"
                )
                trySend(profile)
            }
        awaitClose { registration.remove() }
    }
}
