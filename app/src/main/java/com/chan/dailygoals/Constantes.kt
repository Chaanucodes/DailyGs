package com.chan.dailygoals

import android.Manifest

object Constantes {
    const val CHANNEL_ID = "DailyGoalsServiceChannel"
    const val CHANNEL_ID_REMINDER = "DailyGoalsReminderChannel"
    const val REQ_CODE_AUTH = 2002
    const val WORK_MANAGER_REMINDER_UNIQUE_NAME = "REMINDER_FOR_NOTIFICATIONS"
    const val TOTAL_DAYS_ACTIVE = "Total days active"
    const val TOTAL_TASKS_COMPLETED = "Total completed tasks today"
    const val TOTAL_TASKS_TODAY = "Total tasks today"
    const val ALL_TIME_TASKS = "All time tasks"
    const val ALL_TIME_COMPLETED_TASKS = "All time completed tasks"

    const val DAILY_TASKS_LIMIT = 15L

    const val REQUEST_EXTERNAL_STORAGE = 1
    val PERMISSION_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}