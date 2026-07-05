package dev.kalpeshmore.spendwise.ui.main

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import dev.kalpeshmore.spendwise.R
import dev.kalpeshmore.spendwise.databinding.ActivityMainBinding
import dev.kalpeshmore.spendwise.ui.dialogs.AddTransactionDialog
import dev.kalpeshmore.spendwise.ui.viewModel.SpendwiseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: SpendwiseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this)[SpendwiseViewModel::class.java]

        setupViewPager()
        setupFab()
        setupBackPressHandling()

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            //binding.bottomNavigation.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

            when (item.itemId) {
                R.id.nav_dashboard -> binding.viewPager.currentItem = 0
                R.id.nav_transactions -> binding.viewPager.currentItem = 1
//                R.id.nav_profile -> binding.viewPager.currentItem = 2
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.bottomNavigation.selectedItemId = R.id.nav_dashboard
                    1 -> binding.bottomNavigation.selectedItemId = R.id.nav_transactions
//                    2 -> binding.bottomNavigation.selectedItemId = R.id.nav_profile
                }
                binding.appBarText.text = when (position) {
                    0 -> getString(R.string.app_name)
                    1 -> getString(R.string.transactions)
//                    2 -> getString(R.string.profile)
                    else -> getString(R.string.app_name)
                }
                if (position == 2)
                    binding.fabAddTransaction.hide()
                else binding.fabAddTransaction.show()
            }
        })
    }

    private fun setupViewPager() {
        val adapter = SpendwiseAdapter(this)
        binding.viewPager.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAddTransaction.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            AddTransactionDialog().show(supportFragmentManager, "AddTransaction")
        }
    }

    private fun setupBackPressHandling() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.viewPager.currentItem != 0) {
                    binding.viewPager.currentItem = 0
                } else {
                    showExitConfirmationDialog()
                }
            }
        })
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Exit") { _, _ ->
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}