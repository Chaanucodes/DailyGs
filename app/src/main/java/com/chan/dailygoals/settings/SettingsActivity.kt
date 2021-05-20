package com.chan.dailygoals.settings

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.chan.dailygoals.LoginActivity
import com.chan.dailygoals.R
import com.chan.dailygoals.databinding.SettingsActivityBinding
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    companion object {
        fun newInstance() = SettingsActivity()
    }

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
        supportActionBar?.elevation = 0f
        supportActionBar?.title = "Settings"

        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.settings_activity)

        binding.lifecycleOwner = this
        binding.vModel = viewModel

        binding.tvLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            viewModel.logout {
                Intent(this, LoginActivity::class.java)
                    .apply { startActivity(this) }
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
    }



//    override fun onStop() {
//        (activity as MainActivity).pager.isUserInputEnabled = true
//        (activity as MainActivity).showTab()
//
//        super.onStop()
//    }
//
//    override fun onResume() {
//        (activity as MainActivity).hideTab()
//        (activity as MainActivity).pager.isUserInputEnabled = false
//        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Settings"
//        super.onResume()
//    }

    override fun onResume() {
        super.onResume()

    }


}