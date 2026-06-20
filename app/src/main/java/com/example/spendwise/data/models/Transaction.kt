package com.example.spendwise.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val description: String,
    val category: String,
    val type: TransactionsType,
    val date: Long = System.currentTimeMillis()
)
