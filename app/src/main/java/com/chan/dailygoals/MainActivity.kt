package com.chan.dailygoals

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
        supportActionBar?.elevation = 0f

        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = resources.getColor(R.color.colorAccent)
        if(FirebaseCustomManager.allTasks.isEmpty())
        FirebaseCustomManager.loadAllData()
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.titleFragment) as NavHostFragment? ?: return


    }



    override fun onDestroy() {
        super.onDestroy()
        FirebaseCustomManager.tasksData = mutableMapOf()
        FirebaseCustomManager.allTasks = mutableListOf()
    }
}