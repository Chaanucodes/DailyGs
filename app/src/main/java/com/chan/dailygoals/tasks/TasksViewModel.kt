package com.chan.dailygoals.tasks

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chan.dailygoals.convertToDashDate
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.chan.dailygoals.models.DailyTasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksViewModel(val date : String) : ViewModel() {

    var list = mutableListOf<DailyTasks>()

    var isDataLoaded = MutableLiveData<Boolean>()

    init {
        if(date == System.currentTimeMillis().convertToDashDate())
        loadThisDayData()
        else
            loadThatDate()
    }

    private fun loadThatDate() {
        FirebaseCustomManager.docRef.document(date)
            .get().addOnSuccessListener {docSnap ->
                    (docSnap.data?.getValue("dailyTasks") as MutableMap<String, Int>).forEach {
                        list.add(DailyTasks(it.key, it.value))
                        isDataLoaded.value = true
                    }
                    Log.i("TUSKSMODEAL","$date = ${docSnap.data}")

            }
    }

    fun loadThisDayData() {
        list.clear()
            FirebaseCustomManager.tasksData.forEach {
                list.add(DailyTasks(it.key, it.value))
                isDataLoaded.value = true
            }

    }

    override fun onCleared() {
        isDataLoaded.value = false
        super.onCleared()
    }
}

class TasksViewModelFactory(
    val date : String = System.currentTimeMillis().convertToDashDate()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            return TasksViewModel(date) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}