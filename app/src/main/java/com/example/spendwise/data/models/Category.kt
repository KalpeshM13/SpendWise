package com.example.spendwise.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val name: String,
    val type: TransactionsType,
    val color: Int
)
