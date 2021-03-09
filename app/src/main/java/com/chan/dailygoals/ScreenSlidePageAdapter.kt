package com.chan.dailygoals

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chan.dailygoals.baseFrag.BaseFragment
import com.chan.dailygoals.title.TitleFragment
import com.chan.dailygoals.userData.UserStatsFragment

const val NUM_PAGES = 2
class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> BaseFragment()
            1 -> UserStatsFragment()
            else -> BaseFragment()
        }
    }
}