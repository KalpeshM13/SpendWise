package com.example.spendwise.ui.dashboard

import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.spendwise.R
import com.example.spendwise.data.models.DashboardState
import com.example.spendwise.data.models.ExpenseCategoryData
import com.example.spendwise.databinding.FragmentDashboardBinding
import com.example.spendwise.databinding.FragmentTransactionsBinding
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
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)

            setUsePercentValues(true)
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
            setDrawEntryLabels(false)


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
        if(categories.isEmpty()) return

        val entries = categories.map{category ->
            PieEntry(category.amount.toFloat(), category.category)
        }

        val colors = listOf(
            Color.rgb(64, 89, 120),
            Color.rgb(149, 165, 124),
            Color.rgb(217, 184, 162),
            Color.rgb(191, 134, 134),
            Color.rgb(179, 48, 80),
            Color.rgb(193, 37, 82),
            Color.rgb(255, 102, 0),
            Color.rgb(245, 199, 0)
        )

        val dataset = PieDataSet(entries,"Expense Categories").apply {
            setColors(colors)
            valueFormatter = PercentFormatter(binding.pieChart)
            valueTextSize = 11f
            valueTextColor = Color.WHITE
            PieDataSet.ValuePosition.INSIDE_SLICE
        }

        val pieData = PieData(dataset)
        binding.pieChart.apply {
            data = pieData
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}