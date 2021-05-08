package com.chan.dailygoals.backgroundNotificationService

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.chan.dailygoals.Constantes.CHANNEL_ID
import com.chan.dailygoals.MainActivity
import com.chan.dailygoals.R
import com.chan.dailygoals.convertToDashDate
import com.chan.dailygoals.models.DailyTasks

class TasksService : Service(){

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val dailyTask = intent?.getParcelableExtra<DailyTasks>("inputExtra")
        var taskName = "task"
        dailyTask?.let {
            taskName = it.taskName.toString()
        }

        val bundle = Bundle()
        bundle.putString("date", System.currentTimeMillis().convertToDashDate())
        bundle.putParcelable("taskValue", dailyTask)
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.main_nav)
            .setDestination(R.id.tasksFragment)
            .setComponentName(MainActivity::class.java)
            .setArguments(bundle)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .apply {
                setContentTitle("$taskName in progress")
                setContentText("Touch when task is completed")
                setSmallIcon(R.drawable.ic_notification)
                setAutoCancel(true)
                setContentIntent(pendingIntent)
            }.build()

        startForeground(1, notification)
        return START_NOT_STICKY
//        return super.onStartCommand(intent, flags, startId)
    }
}