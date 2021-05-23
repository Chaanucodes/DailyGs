package com.chan.dailygoals.title

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.chan.dailygoals.MainActivity
import com.chan.dailygoals.R
import com.chan.dailygoals.convertToDashDate
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.chan.dailygoals.settings.SettingsActivity
import com.chan.dailygoals.tasks.AddNewTaskDialogFragment
import com.chan.dailygoals.tasks.DialogFragmentDataCallback
import com.chan.dailygoals.tasks.LoadingBarCallback
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
            if (it){
                mAdapter.updateList(viewModel.list)
                mAdapter.notifyDataSetChanged()
            }

        })

        //Add new task Dialog
        DialogFragmentDataCallback.tempDailyTaskObject.observe(viewLifecycleOwner, Observer {
            it?.let {
                FirebaseCustomManager.writeTodaysData(it) {
                    mAdapter.notifyDataSetChanged()
                    LoadingBarCallback.isLoading.value = false
                    DialogFragmentDataCallback.tempDailyTaskObject.value = null
                    navController.navigate(
                        TitleFragmentDirections.actionTitleFragmentToTasksFragment(
                            System.currentTimeMillis().convertToDashDate()
                        )
                    )
                }
            }
        })
        Log.i("TAGGING_TITLE", "${FirebaseCustomManager.allTasks}")
        floatingActionButton.setOnClickListener {


            //TODO: ADD DIALOG BAR IN TITLE FRAG
            callAddNewTaskFragment()
        }
//        setHasOptionsMenu(true)
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

    private fun hideSoftKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onResume() {
        (requireActivity() as AppCompatActivity). supportActionBar?.title =
            "Welcome, ${FirebaseCustomManager.userName}"
        super.onResume()
    }

}