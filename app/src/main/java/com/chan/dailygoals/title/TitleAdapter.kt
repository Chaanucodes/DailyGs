package com.chan.dailygoals.title

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chan.dailygoals.R
import com.chan.dailygoals.convertToDashDate
import com.chan.dailygoals.databinding.TitleListItemBinding
import com.chan.dailygoals.models.DailyTasks

class TitleAdapter(
    private var tasks: List<DailyTasks>,
    val context: Context,
    private val navigationFun: (str: String) -> Unit

) : RecyclerView.Adapter<TitleAdapter.TaskListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        return TaskListViewHolder.from(parent)
    }

    fun updateList(list: List<DailyTasks>) {
        tasks = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val item = tasks[position]
        Log.i("TITAL_ADAPTAR", "${item.timeStamp}")
        holder.bind(item, navigationFun, context)
    }

    class TaskListViewHolder private constructor(private val binding: TitleListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): TaskListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TitleListItemBinding.inflate(layoutInflater, parent, false)

                return TaskListViewHolder(binding)
            }
        }

        fun bind(
            item: DailyTasks,
            navigationFun: (str: String) -> Unit,
            context: Context
        ) {
            binding.title = item

            when (item.taskName.split(" ")[1].split("-")[0]) {
                in
                "Jan", "Apr", "Jul", "Oct",  -> {


                    binding.progressBarTitleItem.progressDrawable = ContextCompat.getDrawable(
                        context,
                        R.drawable.progress_bg_blue
                    )
                }

                 "Feb", "May", "Aug", "Nov" -> {
                     binding.progressBarTitleItem.progressDrawable = ContextCompat.getDrawable(
                         context,
                         R.drawable.progress_bg
                     )
                }

                "Mar", "Jun", "Sep", "Dec"->{
                    binding.progressBarTitleItem.progressDrawable = ContextCompat.getDrawable(
                        context,
                        R.drawable.progress_bg_orange
                    )
                }
            }
            binding.layoutTitleListItem.setOnClickListener {
                navigationFun(item.timeStamp.convertToDashDate())
            }
            binding.executePendingBindings()
        }


    }
}