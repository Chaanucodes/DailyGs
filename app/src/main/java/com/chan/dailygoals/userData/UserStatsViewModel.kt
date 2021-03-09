package com.chan.dailygoals.userData

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chan.dailygoals.firecloud.FirebaseCustomManager

class UserStatsViewModel : ViewModel() {

    private var _totalDays = MutableLiveData<Int>()
    val totalDays : LiveData<Int>
    get() = _totalDays

    init {
        _totalDays.value = FirebaseCustomManager.daysActive.toInt()
     }
}