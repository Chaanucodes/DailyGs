package com.chan.dailygoals.title

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chan.dailygoals.R
import com.chan.dailygoals.convertToDashDate
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.chan.dailygoals.tasks.TasksListAdapter
import kotlinx.android.synthetic.main.title_fragment.*

class TitleFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var mAdapter: TitleAdapter
    private lateinit var viewModel: TitleViewModel

    companion object{

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.title_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TitleViewModel::class.java)
        viewModel.loadData()

        navController = Navigation.findNavController(view)

        mAdapter = TitleAdapter(viewModel.list){
            navController.navigate(TitleFragmentDirections.actionTitleFragmentToTasksFragment(it))
        }
        title_recycle_view.layoutManager = LinearLayoutManager(requireContext())
        title_recycle_view.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        viewModel.listReady.observe(viewLifecycleOwner, Observer {
            if (it) mAdapter.updateList(viewModel.list)
            mAdapter.notifyDataSetChanged()
        })

        Log.i("TAGGING_TITLE", "${FirebaseCustomManager.allTasks}")
        floatingActionButton.setOnClickListener {
            navController.navigate(TitleFragmentDirections.actionTitleFragmentToTasksFragment(
                System.currentTimeMillis().convertToDashDate()))
        }
    }

    override fun onResume() {
        (requireActivity() as AppCompatActivity). supportActionBar?.title = "Daily progress"
        super.onResume()
    }


}