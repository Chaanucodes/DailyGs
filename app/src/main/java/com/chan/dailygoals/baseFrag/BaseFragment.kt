package com.chan.dailygoals.baseFrag



import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.chan.dailygoals.R
import com.chan.dailygoals.settings.SettingsActivity


//THIS FRAGMENT IS SOLELY CREATED TO SERVE AS A BASE FOR PAGING ADAPTER

class BaseFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.settings_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item.itemId == R.id.action_settings){
            Intent(
                requireActivity(),
                SettingsActivity::class.java
            ).apply {
                startActivity(this)
            }
            true
        }else false
    }



}