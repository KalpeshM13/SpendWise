package com.example.spendwise.ui.main

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.spendwise.R
import com.example.spendwise.databinding.ActivityMainBinding
import com.example.spendwise.ui.dialogs.AddTransactionDialog
import com.example.spendwise.ui.viewModel.SpendwiseViewModel
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

            binding.bottomNavigation.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

            when (item.itemId) {
                R.id.nav_dashboard -> binding.viewPager.currentItem = 0
                R.id.nav_transactions -> binding.viewPager.currentItem = 1
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.bottomNavigation.selectedItemId = R.id.nav_dashboard
                    1 -> binding.bottomNavigation.selectedItemId = R.id.nav_transactions
                }
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
            showAddTransactionDialogue()
        }
    }

    private fun showAddTransactionDialogue() {
        AddTransactionDialog().show(supportFragmentManager, "AddTransaction")
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