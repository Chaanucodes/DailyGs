package com.chan.dailygoals.settings

import androidx.lifecycle.ViewModel
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel : ViewModel() {

    fun logout(logoutDuties : ()->Unit){
        FirebaseCustomManager.clearAll(){
            logoutDuties()
        }

    }
}