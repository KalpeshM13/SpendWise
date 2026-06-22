package com.example.spendwise.ui.transactions

import android.icu.text.NumberFormat
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.spendwise.data.models.Transaction
import com.example.spendwise.data.models.TransactionsType
import com.example.spendwise.databinding.FragmentTransactionDetailBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionDetailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TransactionsViewModel
    private lateinit var transaction: Transaction

    companion object {
        private const val ARG_TRANSACTION_ID = "transaction_id"
        private const val ARG_AMOUNT = "amount"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_CATEGORY = "category"
        private const val ARG_TYPE = "type"
        private const val ARG_DATE = "date"

        fun newInstance(transaction: Transaction): TransactionDetailFragment {
            return TransactionDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TRANSACTION_ID, transaction.id)
                    putDouble(ARG_AMOUNT, transaction.amount)
                    putString(ARG_DESCRIPTION, transaction.description)
                    putString(ARG_CATEGORY, transaction.category)
                    putString(ARG_TYPE, transaction.type.name)
                    putLong(ARG_DATE, transaction.date)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            transaction = Transaction(
                id = args.getInt(ARG_TRANSACTION_ID),
                amount = args.getDouble(ARG_AMOUNT),
                description = args.getString(ARG_DESCRIPTION, ""),
                category = args.getString(ARG_CATEGORY, ""),
                type = TransactionsType.valueOf(args.getString(ARG_TYPE, TransactionsType.EXPENSE.name)),
                date = args.getLong(ARG_DATE)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TransactionsViewModel::class.java]

        bindTransactionData()
        setupDeleteButton()
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun bindTransactionData() {
        val dateFormatter = SimpleDateFormat("EEEE, MMMM dd yyyy", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currencyFormatter = NumberFormat.getCurrencyInstance()

        val formattedAmount = currencyFormatter.format(transaction.amount)
        binding.tvDetailAmount.text = when (transaction.type) {
            TransactionsType.INCOME -> "+$formattedAmount"
            TransactionsType.EXPENSE -> "-$formattedAmount"
        }

        binding.tvDetailAmount.setTextColor(
            requireContext().getColor(
                when (transaction.type) {
                    TransactionsType.INCOME -> android.R.color.holo_green_dark
                    TransactionsType.EXPENSE -> android.R.color.holo_red_dark
                }
            )
        )

        binding.tvDetailDescription.text = transaction.description
        binding.tvDetailCategory.text = transaction.category
        binding.tvDetailType.text = transaction.type.name.lowercase()
            .replaceFirstChar { it.uppercaseChar() }
        binding.tvDetailDate.text = dateFormatter.format(Date(transaction.date))
        binding.tvDetailTime.text = timeFormatter.format(Date(transaction.date))
    }

    private fun setupDeleteButton() {
        binding.btnDeleteTransaction.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            showDeleteConfirmation()
        }
    }

    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete \"${transaction.description}\"?")
            .setPositiveButton("Delete") { _, _ ->
                val deletedTransaction = transaction
                val parentView = parentFragment?.view ?: activity?.findViewById(android.R.id.content)
                viewModel.deleteTransaction(deletedTransaction)
                dismiss()
                parentView?.let { anchorView ->
                    Snackbar.make(anchorView, "Transaction deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            viewModel.insertTransaction(deletedTransaction)
                        }
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}