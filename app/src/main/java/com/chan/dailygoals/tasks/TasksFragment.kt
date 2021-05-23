package com.chan.dailygoals.tasks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
import java.util.*
import kotlin.collections.ArrayList


class TasksFragment : Fragment() {


    private lateinit var viewModel: TasksViewModel
    private lateinit var mAdapter: TasksListAdapter
    private var args: TasksFragmentArgs? = null
    private var date = "date"
    private var dailyTasks: DailyTasks? = null
    var varSpinner: Spinner? = null
    var varSpinnerData: List<String>? = null

    var varScaleX = 0f
    var varScaleY = 0f

    private val startForResult = this.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            handleReceivedData(intent)
        }
    }

    private fun handleReceivedData(intent: Intent?) {
        intent?.let {
            val existing = it.getBooleanExtra("alreadyExists", false)
            if (existing) {
                Toast.makeText(requireActivity(), "This task is already added.",
                    Toast.LENGTH_SHORT).show()
            } else {
                DialogFragmentDataCallback.addTempData(
                    it.getStringExtra("taskName")!!.trim().
                    toLowerCase(Locale.ROOT).capitalize(Locale.ROOT), 0
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        //THIS IS TO ADD A CUSTOM FUNCTIONALITY TO BACK BUTTON ONLY FOR THIS FRAGMENT
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
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

// Create an ArrayAdapter using the string array and a default spinner layout

        //To determine what data to load after fragment initialization
        if (date == "date") {
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
        if (args?.date != System.currentTimeMillis().convertToDashDate()) {
            taskAddbutton.visibility = View.INVISIBLE
            exploreButton.visibility = View.GONE
            mAdapter = TasksListAdapter(false, requireActivity()) {
                mAdapter.submitList(null)
                findNavController().popBackStack()
            }
            mAdapter.notifyAboutChanges()
        } else {
            mAdapter = TasksListAdapter(context = requireActivity()) {
                mAdapter.submitList(null)
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

                if (viewModel.list.isEmpty()) {
                    recycler_view_tasks.visibility = View.GONE
                    text_view_no_data.visibility = View.VISIBLE
                    return@Observer
                } else {
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
            } else if (it != null && it.taskName == "") {
                LoadingBarCallback.isLoading.value = false
                Toast.makeText(activity, "Enter some data", Toast.LENGTH_SHORT).show()
            }
        })


        UpdateProgressCallback.isProgressUpdated.observe(viewLifecycleOwner, Observer {
            if (it.third) {
                mAdapter.updateTask(DailyTasks(it.first, it.second))
                UpdateProgressCallback.isProgressUpdated.value = Triple("", 0, false)
            }
        })

        viewModel.isMyCategoriesLoaded.observe(viewLifecycleOwner, Observer {
            if(it){
                if(drop_down_my_categories.visibility == View.GONE)
                    drop_down_my_categories.visibility = View.VISIBLE

                renderSpinner(viewModel.myCategoriesList)
            }
        })

    }

    private fun renderSpinner(myCategoriesList: ArrayList<String>) {

        myCategoriesList.add(0,"Add from your list")

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(), R.layout.custom_my_categories_spinner_item, myCategoriesList
        )

        adapter.setDropDownViewResource(R.layout.custom_simple_spinner_dropdown_item)
        drop_down_my_categories.adapter = adapter
        drop_down_my_categories.isSelected = false

        drop_down_my_categories.isSelected = false

        drop_down_my_categories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    val s = parent?.getItemAtPosition(position).toString().
                    trim().toLowerCase().capitalize()

                    if(position>0){
                        if (FirebaseCustomManager.tasksData.containsKey(s)) {
                            Toast.makeText(
                                requireActivity(),
                                "This task is already added.",
                                Toast.LENGTH_SHORT).show()
                        } else
                            DialogFragmentDataCallback.addTempData(s, 0)
                        drop_down_my_categories.setSelection(0)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
        }

    }



    //Dialog fragment
    private fun callAddNewTaskFragment() {
        val fragmentManager = activity?.supportFragmentManager

        val transaction = fragmentManager?.beginTransaction()
        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        transaction!!
            .add(android.R.id.content, AddNewTaskDialogFragment() {
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

        if (args?.date == fetchFormattedDate() && mAdapter.totalTasks > 0) {
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