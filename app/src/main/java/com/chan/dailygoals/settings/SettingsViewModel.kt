package com.chan.dailygoals.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel : ViewModel() {

    var notificationButtonCollapsed = MutableLiveData<Float>()

    init {
        notificationButtonCollapsed.value = 8f
    }

    fun logout(logoutDuties : ()->Unit){
        FirebaseCustomManager.clearAll(){
            logoutDuties()
        }

    }
}