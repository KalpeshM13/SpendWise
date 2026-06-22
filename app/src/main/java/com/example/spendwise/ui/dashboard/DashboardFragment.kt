package com.example.spendwise.ui.dashboard

import android.graphics.Color
import android.util.TypedValue
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.spendwise.data.models.DashboardState
import com.example.spendwise.data.models.ExpenseCategoryData
import com.example.spendwise.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.collections.map

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPieChart()
        observeDashboardState()
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)

            holeRadius = 58f
            transparentCircleRadius = 61f
            setTransparentCircleColor(Color.TRANSPARENT)
            setTransparentCircleAlpha(110)

            setUsePercentValues(true)
            setEntryLabelColor(resolveThemeColor(com.google.android.material.R.attr.colorOnSurface))
            setEntryLabelTextSize(12f)
            setDrawEntryLabels(false)
            isRotationEnabled = false

            legend.apply {
                isEnabled = true
                orientation = Legend.LegendOrientation.VERTICAL
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                setDrawInside(false)
                xEntrySpace = 10f
                yEntrySpace = 0f
                yOffset = 0f
                textSize = 12f
                formSize = 16f
                textColor = resolveThemeColor(com.google.android.material.R.attr.colorOnSurface)
            }

            animateY(1400, Easing.EaseInOutQuad)
        }
    }

    private fun observeDashboardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dashboardState.collect { state: DashboardState ->
                updateDashboardUI(state)
            }
        }
    }

    private fun updateDashboardUI(state: DashboardState) {
        binding.apply {
            tvBalance.text = currencyFormatter.format(state.balance)
            tvTotalIncome.text = currencyFormatter.format(state.totalIncome)
            tvTotalExpense.text = currencyFormatter.format(state.totalExpense)

            updatePieChart(state.expenseCategories)
        }
    }

    private fun updatePieChart(categories: List<ExpenseCategoryData>) {
        if (categories.isEmpty()) {
            binding.pieChart.data = null
            binding.pieChart.clear()
            binding.pieChart.invalidate()
            return
        }

        val entries = categories.map{category ->
            PieEntry(category.amount.toFloat(), category.category)
        }

        val colors = listOf(
            Color.rgb(45, 212, 191),   // Teal
            Color.rgb(52, 211, 153),   // Emerald
            Color.rgb(251, 113, 133),  // Rose
            Color.rgb(251, 191, 36),   // Amber
            Color.rgb(56, 189, 248),   // Sky
            Color.rgb(129, 140, 248),  // Indigo
            Color.rgb(167, 139, 250),  // Violet
            Color.rgb(148, 163, 184)   // Slate
        )

        val dataset = PieDataSet(entries,"").apply {
            setColors(colors)
            valueFormatter = PercentFormatter(binding.pieChart)
            valueTextSize = 11f
            valueTextColor = resolveThemeColor(com.google.android.material.R.attr.colorOnSurface)
            PieDataSet.ValuePosition.INSIDE_SLICE
        }

        val pieData = PieData(dataset)
        binding.pieChart.apply {
            data = pieData
            notifyDataSetChanged()
            invalidate()
        }
    }

    private fun resolveThemeColor(attrRes: Int): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}