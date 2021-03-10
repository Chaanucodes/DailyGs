package com.chan.dailygoals

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        setContentView(R.layout.activity_main)

//        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
        supportActionBar?.elevation = 0f

        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = resources.getColor(R.color.colorAccent)
        if(FirebaseCustomManager.allTasks.isEmpty())
        FirebaseCustomManager.loadAllData()

        pager.adapter = pagerAdapter
//        binding.pager.setPageTransformer(ZoomOutPageTransformer())

        val tabLayoutMediator = TabLayoutMediator(tabLayout, pager, TabLayoutMediator.TabConfigurationStrategy{
                tab: TabLayout.Tab, i: Int ->

            when(i) {
                0 -> {
//                    tab.icon = resources.getDrawable(R.drawable.ic_home_bottom_nav)
                    tab.text = "Daily Reports"
                }
                1 -> {
//                    tab.icon = resources.getDrawable(R.drawable.ic_stats_bottom_nav)
                    tab.text = "My stats"
                }
            }

        })

        tabLayoutMediator.attach()


    }


    fun hideTab(){
        tabLayout.visibility = View.GONE
    }

    fun showTab(){
        tabLayout.visibility = View.VISIBLE
    }




    override fun onBackPressed() {
        if (pager.currentItem == 0) {
                super.onBackPressed()
        }else {
            pager.currentItem = pager.currentItem - 1
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        FirebaseCustomManager.tasksData = mutableMapOf()
        FirebaseCustomManager.allTasks = mutableListOf()
    }
}