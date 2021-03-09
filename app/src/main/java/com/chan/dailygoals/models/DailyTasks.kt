package com.chan.dailygoals.models

data class DailyTasks (
    var taskName : String = "",
    var progress : Int = 0,
    var timeStamp : Long = System.currentTimeMillis()
)


