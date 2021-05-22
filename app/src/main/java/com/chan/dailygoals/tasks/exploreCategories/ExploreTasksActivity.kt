package com.chan.dailygoals.tasks.exploreCategories

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.chan.dailygoals.R
import kotlinx.android.synthetic.main.activity_explore_tasks.*


class ExploreTasksActivity : AppCompatActivity() {
    var expandableListAdapter: ExpandableListAdapter? = null
    var expandableListTitle: MutableList<String> = mutableListOf()
    var expandableListDetail: HashMap<String, List<String>> = HashMap<String, List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {

        //Styling of action back and navigation bar
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
        supportActionBar?.elevation = 0f

        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = resources.getColor(R.color.colorAccent)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_tasks)


        expandableListDetail = CategoryData.getData();
        expandableListTitle = ArrayList<String>(expandableListDetail!!.keys);
        expandableListAdapter =
            CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        categoriesList.setAdapter(expandableListAdapter);
        categoriesList.setOnGroupExpandListener { groupPosition ->
        };

        categoriesList.setOnGroupCollapseListener {
        }

        categoriesList.setOnChildClickListener {  parent: ExpandableListView, v: View, groupPosition: Int, childPosition: Int, id: Long ->


            val resultIntent = Intent()

            resultIntent.putExtra("taskName",(expandableListDetail[expandableListTitle[groupPosition]]?.get(
                childPosition)))
            setResult(RESULT_OK, resultIntent)
            finish()


            false
            }

    }

}