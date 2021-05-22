package com.chan.dailygoals

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.chan.dailygoals.settings.SettingsActivity
import com.chan.dailygoals.tasks.LoadingBarCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        setContentView(R.layout.activity_main)

        //Styling of action back and navigation bar
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
        supportActionBar?.elevation = 0f

        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = resources.getColor(R.color.colorAccent)
        if(FirebaseCustomManager.allTasks.isEmpty())
        FirebaseCustomManager.loadAllData()

        pager.adapter = pagerAdapter
//        binding.pager.setPageTransformer(ZoomOutPageTransformer())
//        LoadingBarCallback.isLoading.value = true
        LoadingBarCallback.isLoading.observe(this, Observer {
            if(it){
                progressBar.visibility = View.VISIBLE
            }else{
                if(progressBar.visibility == View.VISIBLE)
                    progressBar.visibility = View.INVISIBLE
            }
        })
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

    // Inflating menu
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        super.onCreateOptionsMenu(menu)
//
//        menuInflater.inflate(R.menu.settings_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        if(item.itemId == R.id.action_settings){
//            Intent(
//                this,
//                SettingsActivity::class.java
//            ).apply {
//                startActivity(this)
//            }
//            return true
//        }else return false
//
//    }
}