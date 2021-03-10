package com.chan.dailygoals

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.signin.SignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val REQ_CODE_AUTH = 2002
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
//        supportActionBar?.elevation = 0f

        animateLayout()
        supportActionBar?.hide()
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
            AuthUI.getInstance().
            createSignInIntentBuilder().
            setIsSmartLockEnabled(false).
            setAvailableProviders(providers).build(),
            REQ_CODE_AUTH
        )

    }

    private fun animateLayout() {
        val colorFrom = resources.getColor(R.color.colorAccent)
        val colorTo = resources.getColor(R.color.colorPrimaryDark)
        val colorLast = resources.getColor(R.color.colorPrimary)
        val colorAnimation =
            ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo, colorLast, colorFrom)
        colorAnimation.duration = 2500 // milliseconds
        colorAnimation.repeatCount = ValueAnimator.INFINITE
        colorAnimation.repeatMode = ValueAnimator.REVERSE

        colorAnimation.addUpdateListener { animator ->
            login_activity_layout.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()
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