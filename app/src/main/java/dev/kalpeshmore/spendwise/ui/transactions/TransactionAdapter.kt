package dev.kalpeshmore.spendwise.ui.transactions

import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.kalpeshmore.spendwise.R
import dev.kalpeshmore.spendwise.data.models.Transaction
import dev.kalpeshmore.spendwise.data.models.TransactionsType
import dev.kalpeshmore.spendwise.databinding.ItemTransactionsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit
): ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionsBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return TransactionViewHolder(binding)
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionsBinding): RecyclerView.ViewHolder(binding.root) {
        private val dateFormatter = SimpleDateFormat("MMMM dd yyyy", Locale.getDefault())
        private val currencyFormatter = NumberFormat.getCurrencyInstance()

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(transaction: Transaction) {
            binding.apply {
                tvDescription.text = transaction.description
                tvCategory.text = transaction.category
                tvDate.text = dateFormatter.format(Date(transaction.date))

                val amount = currencyFormatter.format(transaction.amount)
                tvAmount.text = when(transaction.type) {
                    TransactionsType.INCOME -> "+$amount"
                    TransactionsType.EXPENSE -> "-$amount"
                }

                tvAmount.setTextColor(
                    tvAmount.context.getColor(
                        when(transaction.type) {
                            TransactionsType.INCOME -> R.color.income_green
                            TransactionsType.EXPENSE -> R.color.expense_red
                        }
                    )
                )

                val categoryIconRes = when (transaction.category) {
                    "Food & Dining"     -> R.drawable.dining_24px
                    "Transportation"    -> R.drawable.train_car
                    "Shopping"          -> R.drawable.shopping_cart_24px
                    "Bills & Utilities" -> R.drawable.calculator_bill_24
                    "Healthcare"        -> R.drawable.first_aid_kit_24
                    "Salary"            -> R.drawable.user_salary_24
                    "Freelance"         -> R.drawable.freelance_24
                    "Investments"       -> R.drawable.invest_24
                    else                -> R.drawable.category_24px
                }
                categoryImage.setImageResource(categoryIconRes)
            }
        }
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

private class TransactionDiffCallback: DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }

}