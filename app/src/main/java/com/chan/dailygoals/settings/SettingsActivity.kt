package com.chan.dailygoals.settings

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.chan.dailygoals.CustomPrefManager
import com.chan.dailygoals.LoginActivity
import com.chan.dailygoals.R
import com.chan.dailygoals.databinding.SettingsActivityBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {


    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: SettingsActivityBinding
    private lateinit var prefsManager : CustomPrefManager
    private var enabledNotification = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefsManager = CustomPrefManager(this)
        enabledNotification = prefsManager.readShowNotificationPrefs()

        //Styling of action back and navigation bar
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
        supportActionBar?.elevation = 0f

        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = resources.getColor(R.color.colorAccent)
        supportActionBar?.title = "Settings"


        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.settings_activity)

        //Setting hours shown to the user for Reminders notification
        binding.tvSetReminderDelay.text = prefsManager.readNotificationHoursPrefs().toString()

        //Button display for notification settings button
        if(enabledNotification){
            binding.btnNotificationEnabling.text = "Disable Reminders"
        }else{
            binding.btnNotificationEnabling.text = "Enable Reminders"
        }

        binding.lifecycleOwner = this
        binding.vModel = viewModel


        binding.btnDisplayNotification.setOnClickListener {
            if(binding.llNotificationSettings.visibility == View.GONE) {
                binding.llNotificationSettings.visibility = View.VISIBLE
            }
            else{
                binding.llNotificationSettings.visibility = View.GONE
            }

        }

        binding.tvLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            viewModel.logout {
                Intent(this, LoginActivity::class.java)
                    .apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(this) }
                finish()
            }
        }

        binding.tvAbout.setOnClickListener {
            supportActionBar?.hide()
            val fragmentManager = this.supportFragmentManager

            val transaction = fragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

            transaction
                .add(android.R.id.content, AboutDialogFragment {
                    supportActionBar?.show()
                    this.supportFragmentManager.popBackStackImmediate()
                })
                .addToBackStack(null)
                .commit()
        }

        binding.btnNotificationEnabling.setOnClickListener {
            if(prefsManager.readShowNotificationPrefs()){
                prefsManager.saveShowNotificationPrefs(false)
                binding.btnNotificationEnabling.text = "Enable Reminders"
                binding.tvInfoReminderDelay.visibility = View.GONE
                binding.llRemindersSetTime.visibility = View.GONE
            }else{
                prefsManager.saveShowNotificationPrefs(true)
                binding.btnNotificationEnabling.text = "Disable Reminders"
                binding.tvInfoReminderDelay.visibility = View.VISIBLE
                binding.llRemindersSetTime.visibility = View.VISIBLE
            }
        }

        binding.btnIncreaseReminderDelay.setOnClickListener {
            val hours = binding.tvSetReminderDelay.text.toString().toInt() + 1
            if(hours<13)
            binding.tvSetReminderDelay.text = hours.toString()
            else Toast.makeText(this, "Max. limit is 12 hours", Toast.LENGTH_SHORT).apply {
                this.setGravity(Gravity.TOP, 100, 100)
                show()
            }
        }

        binding.btnDecreaseReminderDelay.setOnClickListener {
            val hours = binding.tvSetReminderDelay.text.toString().toInt() -1
            if(hours>0)
                binding.tvSetReminderDelay.text = hours.toString()
            else Toast.makeText(this, "Min. limit is one hour", Toast.LENGTH_SHORT).apply {
                this.setGravity(Gravity.TOP, 100, 100)
                this.
                show()
            }
        }
    }

    override fun onPause() {
        prefsManager.saveNotificationHoursPrefs(binding.tvSetReminderDelay.text.toString().toInt())
        super.onPause()
    }


    override fun onBackPressed() {
        supportActionBar?.show()
        super.onBackPressed()
    }

}