package dev.kalpeshmore.spendwise.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.kalpeshmore.spendwise.data.database.TransactionTypeConverter

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val description: String,
    val category: String,
    @TypeConverters(TransactionTypeConverter::class)
    val type: TransactionsType,
    val date: Long = System.currentTimeMillis()
)
