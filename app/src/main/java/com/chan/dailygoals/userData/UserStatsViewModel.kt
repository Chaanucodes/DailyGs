package com.chan.dailygoals.userData

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chan.dailygoals.convertToDashDate
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

    private var _weeklyChart = MutableLiveData<HashMap<Float, Float>>()
    val weeklyChart: LiveData<HashMap<Float, Float>>
        get() = _weeklyChart

    init {
        loadValues()
    }

    fun forceLoadValues() {
        LoadingBarCallback.isLoading.value = true
        FirebaseCustomManager.loadDailyAnalytics {
            loadValues()
        }
    }

    fun loadValues() {
        _totalDays.value = FirebaseCustomManager.daysActive.toInt()
        _totalCompletedTasksToday.value = FirebaseCustomManager.totalCompletedTasksToday
        _totalTasksToday.value = FirebaseCustomManager.totalTasksToday
        _allTimeCompletedTasks.value = FirebaseCustomManager.allTimeCompletedTasks
        _allTimeTasks.value = FirebaseCustomManager.allTimeTasks
        loadWeeklyData()
        LoadingBarCallback.isLoading.value = false
    }

    private fun loadWeeklyData() {
        if (FirebaseCustomManager.allTasks.isNotEmpty()) {
            var dateCounter = 0
            var value = 0f
            var tempData = 0f
            val hashMap = HashMap<Float, Float>()
            for (i in 0..6) {
                if (FirebaseCustomManager.allTasks[dateCounter].documentDate.equals(
                        (System.currentTimeMillis() - (86400000L * i)).convertToDashDate(), true
                    )
                ) {
                    value = FirebaseCustomManager.allTasks[dateCounter].progress.toFloat()
                    if(FirebaseCustomManager.allTasks.size-1>dateCounter)
                    dateCounter++
                }
                (System.currentTimeMillis() - (86400000L * i)).convertToDashDate().split("-").apply {
                    tempData = (this[0] + "." +this[1]).toFloat()
                }
                hashMap[tempData] = value
                value = 0f
            }
            _weeklyChart.value = hashMap
        }
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