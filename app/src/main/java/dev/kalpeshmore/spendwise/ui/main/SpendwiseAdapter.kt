package dev.kalpeshmore.spendwise.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.kalpeshmore.spendwise.ui.dashboard.DashboardFragment
import dev.kalpeshmore.spendwise.ui.profile.ProfileFragment
import dev.kalpeshmore.spendwise.ui.transactions.TransactionsFragment

class SpendwiseAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> DashboardFragment()
            1 -> TransactionsFragment()
//            2 -> ProfileFragment()
            else -> throw IllegalArgumentException("Invalid Position $position")
        }
    }

    override fun getItemCount(): Int = 2

}
