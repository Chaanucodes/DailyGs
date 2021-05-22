package com.chan.dailygoals.tasks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chan.dailygoals.MainActivity
import com.chan.dailygoals.R
import com.chan.dailygoals.backgroundNotificationService.TasksService
import com.chan.dailygoals.convertToDashDate
import com.chan.dailygoals.fetchFormattedDate
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.chan.dailygoals.models.DailyTasks
import com.chan.dailygoals.tasks.exploreCategories.ExploreTasksActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_new_task_fragment.view.*
import kotlinx.android.synthetic.main.tasks_fragment.*


class TasksFragment : Fragment() {


    private lateinit var viewModel: TasksViewModel
    private lateinit var mAdapter: TasksListAdapter
    private var args: TasksFragmentArgs? = null
    private var date = "date"
    private var dailyTasks : DailyTasks? = null

    private val startForResult = this.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            handleReceivedData(intent)
        }
    }

    private fun handleReceivedData(intent: Intent?) {
        intent?.let {
            val s = it.getStringExtra("taskName")
            s?.let { it1 ->
                DialogFragmentDataCallback.addTempData(
                    it1.trim().toLowerCase().capitalize(), 0)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
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
        args?.let {
            date = it.date
        }
        dailyTasks = requireArguments().getParcelable<DailyTasks>("taskValue")

        val vModelFactory = TasksViewModelFactory(date)
        viewModel = ViewModelProviders.of(this, vModelFactory).get(TasksViewModel::class.java)

        return inflater.inflate(R.layout.tasks_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)

        recycler_view_tasks.layoutManager = LinearLayoutManager(requireContext())


        //To determine what data to load after fragment initialization
        if(date == "date"){
            text_view_no_data.visibility = View.VISIBLE
            (requireActivity() as AppCompatActivity).stopService(
                Intent(
                    requireActivity(),
                    TasksService::class.java
                )
            )
            return
        }

        //Checking if the task opened is of current date or not
        if(args?.date!= System.currentTimeMillis().convertToDashDate()){
            taskAddbutton.visibility = View.INVISIBLE
            exploreButton.visibility = View.GONE
            mAdapter = TasksListAdapter(viewModel.list, false, requireActivity()){
                findNavController().popBackStack()
            }
            mAdapter.notifyAboutChanges()
        }else{
            mAdapter = TasksListAdapter(viewModel.list, context = requireActivity()){
                findNavController().popBackStack()
            }
            mAdapter.notifyAboutChanges()
        }

        recycler_view_tasks.adapter = mAdapter

        exploreButton.setOnClickListener {
                startForResult.launch(Intent(requireActivity(), ExploreTasksActivity::class.java))
        }

        taskAddbutton.setOnClickListener {
            callAddNewTaskFragment()
//            Log.i("TAG_TASKS_FRAGMENT", DialogFragmentDataCallback.tempDailyTaskObject.value!!.taskName)
        }

        taskDoneButton.setOnClickListener {
            findNavController().popBackStack()
        }



        FirebaseCustomManager.dataChangeNotifier.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.loadThisDayData()
                FirebaseCustomManager.dataChangeNotifier.value = false
                LoadingBarCallback.isLoading.value = false
            }
        })

        //Observe if data is loaded to display the list
        viewModel.isDataLoaded.observe(viewLifecycleOwner, Observer {
            if (it) {

                if(viewModel.list.isEmpty()){
                    recycler_view_tasks.visibility = View.GONE
                    text_view_no_data.visibility = View.VISIBLE
                    return@Observer
                }else{
                    recycler_view_tasks.visibility = View.VISIBLE
                    text_view_no_data.visibility = View.GONE
                }
                mAdapter.submitList(viewModel.list.toMutableList())
                mAdapter.notifyAboutChanges()
                if (dailyTasks != null) {
                    mAdapter.completeTask(dailyTasks!!)
                    Toast.makeText(
                        context,
                        "You completed ${dailyTasks!!.taskName}.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    (requireActivity() as AppCompatActivity).stopService(
                        Intent(requireActivity(), TasksService::class.java)
                    )
                }
                viewModel.isDataLoaded.value = false
            }

        })


        //Getting data from New Dialog Fragment
        DialogFragmentDataCallback.tempDailyTaskObject.observe(viewLifecycleOwner, Observer {
            if (it != null && it.taskName != "") {
//                hideSoftKeyboard(view)
                FirebaseCustomManager.writeTodaysData(it)
                viewModel.list.add(it)
                mAdapter.submitList(viewModel.list.toMutableList())
//                mAdapter.updateList(viewModel.list)
                Log.i("TAKS_FRAGMENT", "${viewModel.list}")
                mAdapter.notifyAboutChanges()
                viewModel.isDataLoaded.value = true
                LoadingBarCallback.isLoading.value = false
            } else if(it != null && it.taskName == ""){
                LoadingBarCallback.isLoading.value = false
                Toast.makeText(activity, "Enter some data", Toast.LENGTH_SHORT).show()
            }
        })

    }

    //Dialog fragment
    private fun callAddNewTaskFragment(){
        val fragmentManager = activity?.supportFragmentManager

        val transaction = fragmentManager?.beginTransaction()
        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        transaction!!
            .add(android.R.id.content, AddNewTaskDialogFragment(){
                hideSoftKeyboard(it)
                activity?.supportFragmentManager?.popBackStackImmediate()
            })
            .addToBackStack(null)
            .commit()
    }

    override fun onStop() {

        mAdapter.notifyAboutChanges()
        DialogFragmentDataCallback.tempDailyTaskObject.value = null
        (activity as MainActivity).pager.isUserInputEnabled = true
        (activity as MainActivity).showTab()

        if(args?.date== fetchFormattedDate()){
            FirebaseCustomManager.updateDailyAnalytics(
                mAdapter.tasksCompleted,
                mAdapter.totalTasks,
                mAdapter.totalTasks - mAdapter.tasksCompleted
            )
        }

        if (dailyTasks != null) {
            requireActivity().finish()
        }
        super.onStop()
    }


    override fun onResume() {
        (activity as MainActivity).hideTab()
        (activity as MainActivity).pager.isUserInputEnabled = false
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Activities"
        super.onResume()
    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.action_settings)
        item.isVisible = false
    }

}