package com.chan.dailygoals

import android.app.Application
import android.content.Intent
import android.util.Log
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.google.firebase.auth.FirebaseAuth

class MainApplication : Application() {

    override fun onCreate() {
        if (FirebaseAuth.getInstance().currentUser!=null)
            FirebaseCustomManager.loadUserName()
            FirebaseCustomManager.loadAnalyticsData()
        super.onCreate()
    }


}