package com.chan.dailygoals.tasks

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chan.dailygoals.R
import com.chan.dailygoals.backgroundNotificationService.TasksService
import com.chan.dailygoals.databinding.TaskItemBinding
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.chan.dailygoals.models.DailyTasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksListAdapter(private var isMutable: Boolean = true,
                       private val context: Context,
                       private val dayDeletionCase : ()->Unit
) : ListAdapter<DailyTasks, TasksListAdapter.TaskListViewHolder>(TasksDiffCall){

    var totalTasks = 0
    var tasksCompleted = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        return TaskListViewHolder.from(parent, context, dayDeletionCase)
    }


//    override fun getItemCount(): Int {
//        return tasks.size
//    }

    fun completeTask(dailyT: DailyTasks){
        currentList.forEachIndexed { i, dl->
            if(dl.taskName == dailyT.taskName){
                currentList[i].progress = 100
                FirebaseCustomManager.updateProgress(dl.taskName, 100)
                notifyAboutChanges()
            }
        }
    }

    fun updateTask(dailyT: DailyTasks){
        currentList.forEachIndexed { i, dl->
            if(dl.taskName == dailyT.taskName){
                currentList[i].progress = dailyT.progress
                notifyAboutChanges()
            }
        }
    }


    fun notifyAboutChanges(){
//        notifyDataSetChanged()

        tasksCompleted = 0
        totalTasks = 0

        currentList?.let{
            currentList.forEach { dt->
            if(dt.progress == 100)
                tasksCompleted++
        }
            totalTasks = currentList.size
        }


    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, isMutable)
    }

    class TaskListViewHolder private constructor(
        private val binding: TaskItemBinding,
        private val context: Context,
        val dayDeletionCase: () -> Unit) : RecyclerView.ViewHolder(binding.root){

        companion object{
            fun from(parent: ViewGroup, context: Context, dayDeletionCase: () -> Unit) : TaskListViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
                binding.seekbarTaskItem.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        binding.percentageTaskItem.text = "$p1%"
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                        UpdateProgressCallback.isProgressUpdated.value = Triple(
                            binding.titleTaskItem.text.toString(),
                            p0!!.progress,
                            true
                        )
                        FirebaseCustomManager.updateProgress(binding.titleTaskItem.text, p0!!.progress)
                    }
                })
                return TaskListViewHolder(binding, context, dayDeletionCase)
            }


        }

        fun bind(item : DailyTasks, isMutable: Boolean){
            binding.task = item
            if(!isMutable) {
                binding.seekbarTaskItem.isEnabled = false
                binding.popupSettingsTaskItem.visibility = View.GONE
            }
            binding.popupSettingsTaskItem.setOnClickListener {
                showPopUp(binding.popupSettingsTaskItem, item)
            }
            binding.executePendingBindings()
        }

        private fun showPopUp(popupSettingsTaskItem: ImageButton, dailyT: DailyTasks) {
            val popupMenu = PopupMenu(context, popupSettingsTaskItem)
            popupMenu.inflate(R.menu.popup_task_item_menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                item?.let {mi->
                    if(mi.itemId == R.id.action_delete_task){
                        LoadingBarCallback.isLoading.value = true
                        FirebaseCustomManager.deleteTask(binding.titleTaskItem.text.toString()){
                            dayDeletionCase()
                            LoadingBarCallback.isLoading.value = false
                        }
                    }else if(mi.itemId == R.id.action_start_task_in_bg){
                        if(binding.percentageTaskItem.text.contains("100")){
                            Toast.makeText(context, "Already completed", Toast.LENGTH_SHORT).show()
                        }else{
                            Intent(context, TasksService::class.java)
                                .apply {
                                    putExtra("inputExtra", dailyT)
                                    (context as AppCompatActivity).startService(this)
                                }
                            Toast.makeText(context, "Click on notification when the task is over",
                                Toast.LENGTH_LONG).show()
                        }

                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    object TasksDiffCall : DiffUtil.ItemCallback<DailyTasks>() {

        override fun areItemsTheSame(oldItem: DailyTasks, newItem: DailyTasks): Boolean {
            return oldItem.taskName == newItem.taskName
        }

        override fun areContentsTheSame(oldItem: DailyTasks, newItem: DailyTasks): Boolean {
            return oldItem.progress == newItem.progress
        }

    }

}