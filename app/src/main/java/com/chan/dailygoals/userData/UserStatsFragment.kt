package com.chan.dailygoals.userData

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.chan.dailygoals.R
import com.chan.dailygoals.databinding.FragmentUserStatsBinding


class UserStatsFragment : Fragment() {

    private lateinit var binding : FragmentUserStatsBinding
    private lateinit var viewModel: UserStatsViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_stats, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProviders.of(this).get(UserStatsViewModel::class.java)
        binding.vModel = viewModel
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
    }


}