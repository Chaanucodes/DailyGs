package com.chan.dailygoals.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chan.dailygoals.MainActivity
import com.chan.dailygoals.R
import com.chan.dailygoals.convertToDashDate
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import kotlinx.android.synthetic.main.tasks_fragment.*


class TasksFragment : Fragment() {


    private lateinit var viewModel: TasksViewModel
    private lateinit var mAdapter: TasksListAdapter
    private lateinit var args: TasksFragmentArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //THIS IS TO ADD A CUSTOM FUNCTIONALITY TO BACK BUTTON ONLY FOR THIS FRAGMENT
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        args = TasksFragmentArgs.fromBundle(requireArguments())
        val vModelFactory = TasksViewModelFactory(args.date)
        viewModel = ViewModelProviders.of(this, vModelFactory).get(TasksViewModel::class.java)

        return inflater.inflate(R.layout.tasks_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)

        recycler_view_tasks.layoutManager = LinearLayoutManager(requireContext())

        //To determine what data to load after fragment initialization
        if(args.date!= System.currentTimeMillis().convertToDashDate()){
            taskAddbutton.visibility = View.INVISIBLE
            mAdapter = TasksListAdapter(viewModel.list, false, requireContext())
            mAdapter.notifyDataSetChanged()
        }else{
            mAdapter = TasksListAdapter(viewModel.list, context = requireContext())
        }

        recycler_view_tasks.adapter = mAdapter

        taskAddbutton.setOnClickListener {
            callAddNewTaskFragment()
//            Log.i("TAG_TASKS_FRAGMENT", DialogFragmentDataCallback.tempDailyTaskObject.value!!.taskName)
        }

        taskDoneButton.setOnClickListener {
            findNavController().popBackStack()
        }

        FirebaseCustomManager.dataChangeNotifier.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.loadThisDayData()
                FirebaseCustomManager.dataChangeNotifier.value = false
            }
        })

        viewModel.isDataLoaded.observe(viewLifecycleOwner, Observer {
            if(it){
                mAdapter.updateList(viewModel.list)
                mAdapter.notifyDataSetChanged()
                viewModel.isDataLoaded.value = false
            }

        })


        //Getting data from New Dialog Fragment
        DialogFragmentDataCallback.tempDailyTaskObject.observe(viewLifecycleOwner, Observer {
            if(it!=null && it.taskName!= ""){
//                viewModel.list.forEach { dt->
//                    if(dt.taskName == )
//                }
                Log.i("TASKS_FRAGMANT", "${it.taskName.toLowerCase().capitalize()}")
                FirebaseCustomManager.writeTodaysData(it)
                viewModel.list.add(it)
                mAdapter.updateList(viewModel.list)
                Log.i("TAKS_FRAGMENT", "${viewModel.list}")
                mAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun callAddNewTaskFragment(){
        val fragmentManager = activity?.supportFragmentManager

        val transaction = fragmentManager?.beginTransaction()
        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        transaction!!
            .add(android.R.id.content, AddNewTaskFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onStop() {
        DialogFragmentDataCallback.tempDailyTaskObject.value = null
        (activity as MainActivity).showTab()
        super.onStop()
    }

    override fun onResume() {
        (activity as MainActivity).hideTab()
        (requireActivity() as AppCompatActivity). supportActionBar?.title = "Activities"
        super.onResume()
    }

}