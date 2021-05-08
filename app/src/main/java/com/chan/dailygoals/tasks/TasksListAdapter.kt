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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chan.dailygoals.R
import com.chan.dailygoals.backgroundNotificationService.TasksService
import com.chan.dailygoals.databinding.TaskItemBinding
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.chan.dailygoals.models.DailyTasks

class TasksListAdapter(private var tasks: List<DailyTasks>,
                       private var isMutable: Boolean = true,
                       private val context: Context
) : RecyclerView.Adapter<TasksListAdapter.TaskListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        return TaskListViewHolder.from(parent, context)
    }

    fun updateList(list : List<DailyTasks>){
        tasks = list
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return tasks.size
    }

    fun completeTask(dailyT: DailyTasks){
        tasks.forEachIndexed { i, dl->
            if(dl.taskName == dailyT.taskName){
                tasks[i].progress = 100
                FirebaseCustomManager.updateProgress(dl.taskName, 100)
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val item = tasks[position]
        holder.bind(item, isMutable)
    }

    class TaskListViewHolder private constructor(private val binding: TaskItemBinding,
    private val context: Context) : RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent: ViewGroup, context: Context) : TaskListViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
                binding.seekbarTaskItem.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        binding.percentageTaskItem.setText("$p1%")
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                        FirebaseCustomManager.updateProgress(binding.titleTaskItem.text, p0!!.progress)
                    }
                })
                return TaskListViewHolder(binding, context)
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
                        FirebaseCustomManager.deleteTask(binding.titleTaskItem.text.toString())
                    }else if(mi.itemId == R.id.action_start_task_in_bg){
                        Intent(context, TasksService::class.java)
                            .apply {
                                putExtra("inputExtra", dailyT)
                                (context as AppCompatActivity).startService(this)
                            }
                    }
                }
                true
            }
            popupMenu.show()
        }


    }
}