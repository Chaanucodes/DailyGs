package com.chan.dailygoals

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.chan.dailygoals.Constantes.CHANNEL_ID
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.google.firebase.auth.FirebaseAuth

class MainApplication : Application() {

    override fun onCreate() {
        if (FirebaseAuth.getInstance().currentUser!=null)
            FirebaseCustomManager.loadUserName()
            FirebaseCustomManager.loadDaysActive()

        createNotificationChannel()

        super.onCreate()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Daily goals channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }


}