package com.chan.dailygoals.title

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.chan.dailygoals.models.DailyTasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                listReady.value = true
            }
    }
}