package com.chan.dailygoals.userData

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.chan.dailygoals.tasks.LoadingBarCallback

class UserStatsViewModel : ViewModel() {

    private var _totalDays = MutableLiveData<Int>()
    val totalDays: LiveData<Int>
        get() = _totalDays

    private var _totalTasksToday = MutableLiveData<Int>()
    val totalTasksToday: LiveData<Int>
        get() = _totalTasksToday

    private var _totalCompletedTasksToday = MutableLiveData<Int>()
    val totalCompletedTasksToday: LiveData<Int>
        get() = _totalCompletedTasksToday

    private var _allTimeTasks = MutableLiveData<Int>()
    val allTimeTasks: LiveData<Int>
        get() = _allTimeTasks

    private var _allTimeCompletedTasks = MutableLiveData<Int>()
    val allTimeCompletedTasks: LiveData<Int>
        get() = _allTimeCompletedTasks

    init {
        loadValues()
    }

    fun forceLoadValues(){
        LoadingBarCallback.isLoading.value = true
        FirebaseCustomManager.loadDailyAnalytics{
            loadValues()
        }
    }

    fun loadValues() {
        _totalDays.value = FirebaseCustomManager.daysActive.toInt()
        _totalCompletedTasksToday.value = FirebaseCustomManager.totalCompletedTasksToday
        _totalTasksToday.value = FirebaseCustomManager.totalTasksToday
        _allTimeCompletedTasks.value = FirebaseCustomManager.allTimeCompletedTasks
        _allTimeTasks.value = FirebaseCustomManager.allTimeTasks
        LoadingBarCallback.isLoading.value = false
    }

    override fun onCleared() {
        _totalDays.value = 0
        _totalCompletedTasksToday.value = 0
        _totalTasksToday.value = 0
        _allTimeTasks.value = 0
        _allTimeCompletedTasks.value = 0
        super.onCleared()
    }
}