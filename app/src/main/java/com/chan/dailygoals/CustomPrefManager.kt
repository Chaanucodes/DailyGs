package com.chan.dailygoals

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity

class CustomPrefManager(context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        "CUSTOM_PREFS", Context.MODE_PRIVATE)

    fun readShowNotificationPrefs() : Boolean{
        return sharedPref.getBoolean("SHOW_NOTIFICATION",true)
    }

    fun saveShowNotificationPrefs(change : Boolean){
        sharedPref.edit().apply {
            putBoolean("SHOW_NOTIFICATION", change)
            apply()
        }
    }

    fun readPendingTasksPrefs() : String? {
        return sharedPref.getString("NOTIF_TASK_NAME","")
    }

    fun savePendingTasksPrefs(name : String){
        sharedPref.edit().apply {
            putString("NOTIF_TASK_NAME", name)
            apply()
        }
    }

    fun readNotificationHoursPrefs() : Int {
        return sharedPref.getInt("NOTIF_HOURS",3)
    }

    fun saveNotificationHoursPrefs(hours : Int){
        sharedPref.edit().apply {
            putInt("NOTIF_HOURS", hours)
            apply()
        }
    }

    fun readNotificationDatePrefs() : String? {
        return sharedPref.getString("NOTIF_TIME",fetchFormattedDate())
    }

    fun saveNotificationDatePrefs(time : String){
        sharedPref.edit().apply {
            putString("NOTIF_TIME", time)
            apply()
        }
    }
}