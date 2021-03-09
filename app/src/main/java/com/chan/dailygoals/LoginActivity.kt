package com.chan.dailygoals

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val REQ_CODE_AUTH = 2002
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
        supportActionBar?.elevation = 0f

        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = resources.getColor(R.color.colorAccent)

        if(FirebaseAuth.getInstance().currentUser != null){
            buttonLogin.visibility = View.GONE
            loading_text_view.visibility = View.VISIBLE
            FirebaseCustomManager.loadTodaysData()
            FirebaseCustomManager.loadAllData {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
            }
        }
        buttonLogin.setOnClickListener {
            logon()
        }
    }

    private fun logon(){
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
            REQ_CODE_AUTH
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQ_CODE_AUTH){
            FirebaseCustomManager.loadAnalyticsData()
            FirebaseCustomManager.loadTodaysData()
            FirebaseCustomManager.loadAllData(
                ({
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                })
            )


        }
    }
}