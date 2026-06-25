package dev.kalpeshmore.spendwise.ui.profile

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.kalpeshmore.spendwise.R
import dev.kalpeshmore.spendwise.databinding.FragmentManageCategoriesBinding
import kotlinx.coroutines.launch

class ManageCategoriesFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentManageCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ManageCategoriesViewModel by viewModels()
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        observeCategories()
    }

    private fun setupRecycler() {
        categoriesAdapter = CategoriesAdapter(
            onDeleteClick = { category ->
                binding.mcRecyclerView.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY
                )
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to permanently delete '${category.name}'?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteCategory(category)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onEditClick = { category ->
                binding.mcRecyclerView.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY
                )
                // TODO: Handle edit click if needed
            }
        )

        binding.mcRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoriesAdapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allCategories.collect { categories ->
                categoriesAdapter.submitList(categories)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}