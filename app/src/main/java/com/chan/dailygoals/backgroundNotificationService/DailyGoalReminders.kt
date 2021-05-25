package com.chan.dailygoals.backgroundNotificationService

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.chan.dailygoals.*
import com.chan.dailygoals.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class DailyGoalReminders(private val appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams){

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    fun delayedInit() {

        applicationScope.launch {
        }

    }

    override fun doWork(): Result {

//        val sharedPreferences = appContext.getSharedPreferences("CUSTOM_PREFS", Context.MODE_PRIVATE)
        val prefsManager = CustomPrefManager(appContext)
        if(!prefsManager.readPendingTasksPrefs().isNullOrEmpty() &&
                prefsManager.readShowNotificationPrefs()){
                    if(prefsManager.readNotificationDatePrefs()!= fetchFormattedDate()) return Result.failure()


            val bundle = Bundle()
            bundle.putString("date", System.currentTimeMillis().convertToDashDate())
            val pendingIntent = NavDeepLinkBuilder(applicationContext)
                .setGraph(R.navigation.main_nav)
                .setDestination(R.id.tasksFragment)
                .setComponentName(MainActivity::class.java)
                .setArguments(bundle)
                .createPendingIntent()
            val notification = NotificationCompat.Builder(appContext, Constantes.CHANNEL_ID)
                .apply {
                    setContentTitle("${prefsManager.readPendingTasksPrefs()} is still pending")
                    setContentText("Go check how much you already did!")
                    this.color = appContext.getColor(R.color.colorPrimary)
                    setSmallIcon(R.drawable.ic_launcher_foreground)
                    setAutoCancel(true)
                    setContentIntent(pendingIntent)
                    setColorized(true)
                }

            with(NotificationManagerCompat.from(applicationContext)){
                notify(2, notification.build())
            }
        }


        return Result.success()

//        val myWorkRequest = PeriodicWorkRequestBuilder<>(1, TimeUnit.DAYS)
//            .setConstraints(constraints)
//            .build()


//        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
//            ReminderNote.WORK_NAME,
//            ExistingPeriodicWorkPolicy.KEEP,
//            myWorkRequest)

    }
}