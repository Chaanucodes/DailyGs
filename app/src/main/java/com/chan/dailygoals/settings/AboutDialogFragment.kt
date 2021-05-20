package com.chan.dailygoals.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.chan.dailygoals.R
import com.chan.dailygoals.databinding.AboutFragLayoutBinding
import com.google.android.material.snackbar.Snackbar


class AboutDialogFragment(val appBarVisible: ()->Unit) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as dialog or embedded fragment
        val binding : AboutFragLayoutBinding = DataBindingUtil.inflate(
            inflater, R.layout.about_frag_layout, container, false)

        binding.buttonRate.setOnClickListener {
            Snackbar.make(it, "In future, you will be redirected to play store.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Okay", View.OnClickListener {
                    dismiss()
                    appBarVisible()
                })
                .show()

        }

        binding.layoutAboutFragment.setOnClickListener {  }
        return binding.root
    }

}