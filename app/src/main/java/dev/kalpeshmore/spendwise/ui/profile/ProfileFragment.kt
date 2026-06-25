package dev.kalpeshmore.spendwise.ui.profile

import android.graphics.Color
import android.util.TypedValue
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat.performHapticFeedback
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.kalpeshmore.spendwise.R
import dev.kalpeshmore.spendwise.data.models.DashboardState
import dev.kalpeshmore.spendwise.data.models.ExpenseCategoryData
import dev.kalpeshmore.spendwise.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private var selectedMonth = Calendar.getInstance().get(Calendar.MONTH)
    private var selectedYear = Calendar.getInstance().get(Calendar.YEAR)
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val versionName = requireContext()
            .packageManager
            .getPackageInfo(requireContext().packageName, 0)
            .versionName
        binding.appVersion.text = getString(R.string.app_version, versionName)

        updateMonthButtonText()
        setupPieChart()
        observeDashboardState()

        binding.manageCategories.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            ManageCategoriesFragment().show(parentFragmentManager, "ManageCategoriesFragment")
        }

        binding.editProfile.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            EditProfileFragment().show(parentFragmentManager, "EditProfileFragment")
        }

        binding.monthPicker.setOnClickListener {
            showMonthPickerDialog()
        }
    }

    private fun updateMonthButtonText() {
        val monthName = DateFormatSymbols().shortMonths[selectedMonth]
        binding.monthPicker.text = "$monthName $selectedYear"
    }

    private fun showMonthPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_month_picker, null)
        val tvYear = dialogView.findViewById<TextView>(R.id.tvYear)
        val btnPrevYear = dialogView.findViewById<View>(R.id.btnPrevYear)
        val btnNextYear = dialogView.findViewById<View>(R.id.btnNextYear)
        val monthGrid = dialogView.findViewById<GridLayout>(R.id.monthGrid)

        var pickerYear = selectedYear
        val monthNames = DateFormatSymbols().shortMonths

        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)
        val currentMonth = now.get(Calendar.MONTH)

        fun isFutureMonth(month: Int, year: Int): Boolean {
            return year > currentYear || (year == currentYear && month > currentMonth)
        }

        // Build dialog first, then reference it in cell click listeners
        val dialog = MaterialAlertDialogBuilder(
            requireContext(),
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog
        )
            .setTitle(R.string.pick_month)
            .setView(dialogView)
            .create()

        fun buildGrid() {
            monthGrid.removeAllViews()
            val context = requireContext()

            for (i in 0 until 12) {
                val isFuture = isFutureMonth(i, pickerYear)
                val cell = TextView(context).apply {
                    text = monthNames[i]
                    textSize = 14f
                    typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                    gravity = Gravity.CENTER
                    setPadding(0, 28, 0, 28)
                    isEnabled = !isFuture
                    alpha = if (isFuture) 0.3f else 1f
                    background = ContextCompat.getDrawable(context, R.drawable.bg_month_item)

                    setTextColor(
                        if (isFuture) ContextCompat.getColor(context, R.color.text_secondary)
                        else ContextCompat.getColor(context, R.color.text_primary)
                    )

                    if (!isFuture) {
                        setOnClickListener {
                            selectedMonth = i
                            selectedYear = pickerYear
                            updateMonthButtonText()
                            onMonthSelected(selectedMonth, selectedYear)
                            dialog.dismiss()
                        }
                    }
                }

                val param = GridLayout.LayoutParams().apply {
                    columnSpec = GridLayout.spec(i % 3, 1f)
                    rowSpec = GridLayout.spec(i / 3)
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    setMargins(4, 4, 4, 4)
                }
                monthGrid.addView(cell, param)
            }
        }

        fun updateYear() {
            tvYear.text = pickerYear.toString()
            btnNextYear.visibility = if (pickerYear >= currentYear) View.INVISIBLE else View.VISIBLE
        }

        updateYear()
        buildGrid()

        btnPrevYear.setOnClickListener {
            pickerYear--
            // Clamp if navigating back to current year with a future month highlighted
            if (pickerYear == currentYear && selectedMonth > currentMonth) {
                selectedMonth = currentMonth
            }
            updateYear()
            buildGrid()
        }

        btnNextYear.setOnClickListener {
            if (pickerYear < currentYear) {
                pickerYear++
                updateYear()
                buildGrid()
            }
        }

        dialog.show()
    }

    private fun onMonthSelected(month: Int, year: Int) {
        viewModel.setMonthAndYear(month, year)
    }

    private fun setupPieChart() {
        binding.profilePieChart.apply {
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
            profileTotalIncome.text = currencyFormatter.format(state.totalIncome)
            profileTotalExpense.text = currencyFormatter.format(state.totalExpense)

            updatePieChart(state.expenseCategories)
        }
    }

    private fun updatePieChart(categories: List<ExpenseCategoryData>) {
        if (categories.isEmpty()) {
            binding.profilePieChart.data = null
            binding.profilePieChart.clear()
            binding.profilePieChart.invalidate()
            return
        }

        val entries = categories.map { category ->
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

        val dataset = PieDataSet(entries, "").apply {
            setColors(colors)
            valueFormatter = PercentFormatter(binding.profilePieChart)
            valueTextSize = 11f
            valueTextColor = resolveThemeColor(com.google.android.material.R.attr.colorOnSurface)
            PieDataSet.ValuePosition.INSIDE_SLICE
        }

        val pieData = PieData(dataset)
        binding.profilePieChart.apply {
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