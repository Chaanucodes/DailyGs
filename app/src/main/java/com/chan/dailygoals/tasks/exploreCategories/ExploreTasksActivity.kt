package com.chan.dailygoals.tasks.exploreCategories

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chan.dailygoals.R
import kotlinx.android.synthetic.main.activity_explore_tasks.*


class ExploreTasksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_tasks)

        testButton.setOnClickListener {
            val resultIntent = Intent()

            resultIntent.putExtra("taskName", "Fishing")
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}