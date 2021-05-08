package com.chan.dailygoals.tasks

import androidx.lifecycle.MutableLiveData
import com.chan.dailygoals.models.DailyTasks

object DialogFragmentDataCallback {
    var tempDailyTaskObject  = MutableLiveData<DailyTasks>()


    fun addTempData(name : String, progress : Int){
        tempDailyTaskObject.value = DailyTasks(name, progress)
    }
}