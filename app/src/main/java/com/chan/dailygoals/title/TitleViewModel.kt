package com.chan.dailygoals.title

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.chan.dailygoals.models.DailyTasks

class TitleViewModel() : ViewModel() {
    var list = mutableListOf<DailyTasks>()

    var listReady = MutableLiveData<Boolean>()

    init {
        listReady.value = false
    }

    fun loadData(){
        list.clear()

            FirebaseCustomManager.allTasks.forEach {
                list.add(DailyTasks(it.taskName, it.progress, it.timeStamp))
            }
        listReady.value = true
    }
}