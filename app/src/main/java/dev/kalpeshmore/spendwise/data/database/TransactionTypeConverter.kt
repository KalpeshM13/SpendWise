package dev.kalpeshmore.spendwise.data.database

import androidx.room.TypeConverter
import dev.kalpeshmore.spendwise.data.models.TransactionsType

class TransactionTypeConverter {
    @TypeConverter
    fun fromTransactionType(type: TransactionsType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toTransactionType(value: String?): TransactionsType? {
        return value?.let { TransactionsType.valueOf(it) }
    }
}