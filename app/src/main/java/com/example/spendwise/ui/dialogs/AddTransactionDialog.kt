package com.example.spendwise.ui.dialogs

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.graphics.toColorInt
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.spendwise.R
import com.example.spendwise.data.models.Category
import com.example.spendwise.data.models.Transaction
import com.example.spendwise.data.models.TransactionsType
import com.example.spendwise.databinding.FragmentAddTransactionDialogBinding
import com.example.spendwise.ui.viewModel.SpendwiseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class AddTransactionDialog : DialogFragment() {
    private var _binding: FragmentAddTransactionDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SpendwiseViewModel
    private var currentType = TransactionsType.EXPENSE
    private var categories = listOf<Category>()

    private val addCustomLabel = "＋ Add Custom Category…"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[SpendwiseViewModel::class.java]

        setupTypeSelection()
        setupCategorySpinner()
        setupInputListeners()
        setupButtons()
        observeCategories()
    }

    private fun setupInputListeners() {
        binding.etAmount.doOnTextChanged { _, _, _, _ ->
            binding.etAmount.error = null
        }
        binding.etDescription.doOnTextChanged { _, _, _, _ ->
            binding.etDescription.error = null
        }
        binding.spinnerCategory.doOnTextChanged { _, _, _, _ ->
            binding.spinnerCategory.error = null
        }
    }

    private fun setupTypeSelection() {
        binding.typeTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentType = when (tab?.position) {
                    0 -> TransactionsType.EXPENSE
                    1 -> TransactionsType.INCOME
                    else -> TransactionsType.EXPENSE
                }

                binding.typeTabLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                observeCategories()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )
        binding.spinnerCategory.setAdapter(adapter)

        binding.spinnerCategory.setOnItemClickListener { _, _, position, _ ->
            val currentAdapter = binding.spinnerCategory.adapter
            if (currentAdapter != null && position == currentAdapter.count - 1) {
                binding.spinnerCategory.setText("", false)
                showAddCustomCategoryDialog()
            } else {
                binding.spinnerCategory.error = null
            }
        }
    }

    private fun showAddCustomCategoryDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "Category name"
            setSingleLine()
        }

        val container = FrameLayout(requireContext()).apply {
            val paddingPx = (16 * resources.displayMetrics.density).toInt()
            setPadding(paddingPx, paddingPx / 2, paddingPx, 0)
            addView(editText)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Custom Category")
            .setMessage("Enter a name for the new category. It will be saved for future use.")
            .setView(container)
            .setPositiveButton("Add") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    saveCustomCategory(name)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun saveCustomCategory(name: String) {
        val newCategory = Category(
            name = name,
            type = currentType,
            color = "#2196F3".toColorInt()
        )
        viewModel.addCategory(newCategory)

        binding.spinnerCategory.setText(name, false)
        binding.spinnerCategory.error = null
    }

    private fun setupButtons() {
        binding.saveButton.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (validateInput()) {
                saveTransaction()
                dismiss()
            }
        }

        binding.cancelButton.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            dismiss()
        }
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCategoriesByType(currentType).collect { newCategories ->
                categories = newCategories
                updateCategorySpinner()
            }
        }
    }

    private fun updateCategorySpinner() {
        val displayItems = categories.map { it.name }.toMutableList()
        displayItems.add(addCustomLabel)

        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_dropdown_item_1line,
            displayItems
        )
        binding.spinnerCategory.setAdapter(adapter)
    }

    private fun saveTransaction() {
        val amount = binding.etAmount.text.toString().toDouble()
        val description = binding.etDescription.text.toString()
        val category = binding.spinnerCategory.text.toString()

        val transaction = Transaction(
            amount = amount,
            description = description,
            category = category,
            type = currentType,
            date = System.currentTimeMillis()
        )

        viewModel.addTransaction(transaction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateInput(): Boolean {
        var isValid = true

        val amount = binding.etAmount.text.toString()
        if (amount.isEmpty() || amount.toDoubleOrNull() == null) {
            binding.etAmount.error = getString(R.string.error_invalid_amount)
            isValid = false
        }

        if (binding.etDescription.text.toString().isEmpty()) {
            binding.etDescription.error = "Empty Description"
            isValid = false
        }

        val selectedCategory = binding.spinnerCategory.text.toString()
        if (selectedCategory.isEmpty() || selectedCategory == addCustomLabel) {
            binding.spinnerCategory.error = "Please select or add a category"
            isValid = false
        }

        return isValid
    }
}