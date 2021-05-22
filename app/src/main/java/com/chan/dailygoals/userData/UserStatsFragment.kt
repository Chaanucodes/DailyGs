package com.chan.dailygoals.userData

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.chan.dailygoals.*
import com.chan.dailygoals.databinding.FragmentUserStatsBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.formatter.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user_stats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class UserStatsFragment : Fragment() {

    private lateinit var binding: FragmentUserStatsBinding
    private lateinit var viewModel: UserStatsViewModel
    private lateinit var lineChart : LineChart



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

        CoroutineScope(Dispatchers.IO).launch {
            verifyStoragePermission()
        }

        refresh_layout_user_stats_fragment.setOnRefreshListener {
            viewModel.forceLoadValues()
            refresh_layout_user_stats_fragment.isRefreshing = false
        }


        viewModel.totalTasksToday.observe(viewLifecycleOwner, Observer {
            if (it < 1) return@Observer
            else setDailyPieView(
                viewModel.totalCompletedTasksToday.value!!.toFloat(),
                viewModel.totalTasksToday.value!!.toFloat(),
                "Today's data",
                pieChartDailyRecord,
                "Add some tasks to get stats"
            )
        })

        viewModel.allTimeTasks.observe(viewLifecycleOwner, Observer {
            if (it < 1) return@Observer
            else setDailyPieView(
                viewModel.allTimeCompletedTasks.value!!.toFloat(),
                viewModel.allTimeTasks.value!!.toFloat(),
                "All time data",
                pieChartAllTimeRecord,
                "Do some tasks today and come tomorrow to get more stats"
            )
        })


        viewModel.weeklyChart.observe(viewLifecycleOwner, {
            if (it.size > 0) {
                setBar(lineChartWeeklyTimeRecord,
                    viewModel.weeklyChart.value!!,
                requireContext())
            }
        })
//        viewModel.weeklyChart.observe(viewLifecycleOwner, Observer {hash->
//            val list = ArrayList<Entry>()
//            hash.forEach {
//                list.add(LineChart)
//            }
//
//        })





        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }



    override fun onResume() {
        viewModel.loadValues()
        (activity as MainActivity).pager.isUserInputEnabled = false
        super.onResume()
    }

    override fun onPause() {
        (activity as MainActivity).pager.isUserInputEnabled = true
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.share_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item.itemId == R.id.action_share){

            binding.scrollViewFragmentUserStats.scrollToDescendant(binding.pieChartAllTimeRecord)
           val file = takeScreenShot(
               binding.scrollViewFragmentUserStats,
               File(
                   requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FilShare"
               )
           )
            shareScreenShot(file)
            true
        }else false
    }

    //Share ScreenShot
    private fun shareScreenShot(imageFile: File) {
        val uri = FileProvider.getUriForFile(
            requireActivity(), BuildConfig.APPLICATION_ID.toString() + ".provider",
            imageFile
        )
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_TEXT, "Check my progress")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        try {
            this.startActivity(Intent.createChooser(intent, "Share With"))
//            binding.scrollViewFragmentUserStats.setBackgroundResource(0)
        } catch (e: ActivityNotFoundException) {
//            binding.scrollViewFragmentUserStats.setBackgroundResource(0)
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyStoragePermission() {
        val permission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                Constantes.PERMISSION_STORAGE,
                Constantes.REQUEST_EXTERNAL_STORAGE
            )
        }
    }

}