package com.example.spendwise.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spendwise.ui.dashboard.DashboardFragment
import com.example.spendwise.ui.transactions.TransactionsFragment

class SpendwiseAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> DashboardFragment()
            1 -> TransactionsFragment()
            else -> throw IllegalArgumentException("Invalid Position $position")
        }
    }

    override fun getItemCount(): Int = 2

}
