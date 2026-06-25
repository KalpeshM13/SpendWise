package dev.kalpeshmore.spendwise.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.kalpeshmore.spendwise.data.models.Category
import dev.kalpeshmore.spendwise.databinding.ItemCategoriesBinding

class CategoriesAdapter(
    private val onDeleteClick: (Category) -> Unit,
    private val onEditClick: (Category) -> Unit
) : ListAdapter<Category, CategoriesAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(private val binding: ItemCategoriesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.categoryName.text = category.name
            
            val categoryIconRes = when (category.name) {
                "Food & Dining"     -> dev.kalpeshmore.spendwise.R.drawable.dining_24px
                "Transportation"    -> dev.kalpeshmore.spendwise.R.drawable.train_car
                "Shopping"          -> dev.kalpeshmore.spendwise.R.drawable.shopping_cart_24px
                "Bills & Utilities" -> dev.kalpeshmore.spendwise.R.drawable.calculator_bill_24
                "Healthcare"        -> dev.kalpeshmore.spendwise.R.drawable.first_aid_kit_24
                "Salary"            -> dev.kalpeshmore.spendwise.R.drawable.user_salary_24
                "Freelance"         -> dev.kalpeshmore.spendwise.R.drawable.freelance_24
                "Investments"       -> dev.kalpeshmore.spendwise.R.drawable.invest_24
                else                -> dev.kalpeshmore.spendwise.R.drawable.category_24px
            }
            binding.categoryImage.setImageResource(categoryIconRes)
            
            binding.deleteCategory.setOnClickListener {
                onDeleteClick(category)
            }
            binding.editCategory.setOnClickListener {
                onEditClick(category)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
