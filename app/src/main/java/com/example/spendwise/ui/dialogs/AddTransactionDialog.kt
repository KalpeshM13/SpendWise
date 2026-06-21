package com.example.spendwise.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.spendwise.R
import com.example.spendwise.data.models.Category
import com.example.spendwise.data.models.Transaction
import com.example.spendwise.data.models.TransactionsType
import com.example.spendwise.databinding.FragmentAddTransactionDialogBinding
import com.example.spendwise.ui.viewModel.SpendwiseViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import kotlin.toString

class AddTransactionDialog : DialogFragment() {
    private var _binding: FragmentAddTransactionDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SpendwiseViewModel
    private var currentType = TransactionsType.EXPENSE
    private var categories = listOf<Category>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddTransactionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[SpendwiseViewModel::class.java]

        setupTypeSelection()
        setupCategorySpinner()
        setupButtons()
        observeCategories()
    }

    private fun setupTypeSelection() {
        binding.typeTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentType = when(tab?.position) {
                    0 -> TransactionsType.EXPENSE
                    1 -> TransactionsType.INCOME
                    else -> TransactionsType.EXPENSE
                }
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
    }

    private fun setupButtons() {
        binding.saveButton.setOnClickListener {
            if(validateInput()) {
                saveTransaction()
                dismiss()
            }
        }

        binding.cancelButton.setOnClickListener {
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
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_dropdown_item_1line,
            categories.map { it.name }
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
        if(amount.isEmpty() || amount.toDoubleOrNull() == null) {
            binding.etAmount.error = getString(R.string.error_invalid_amount)
            isValid = false
        }

        if(binding.etDescription.text.toString().isEmpty()) {
            binding.etDescription.error = "Empty Description"
            isValid = false
        }

        if(binding.spinnerCategory.text.toString().isEmpty()) {
            binding.spinnerCategory.error = "Please Select a Category"
            isValid = false
        }

        return isValid
    }
}