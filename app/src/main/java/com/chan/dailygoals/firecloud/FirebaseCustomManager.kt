package com.chan.dailygoals.firecloud

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.chan.dailygoals.convertLongToDateString
import com.chan.dailygoals.convertToDashDate
import com.chan.dailygoals.fetchFormattedDate
import com.chan.dailygoals.getAverage
import com.chan.dailygoals.models.DailyTasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object FirebaseCustomManager {

    val docRef = FirebaseFirestore.getInstance().collection("allUsers")
        .document("${FirebaseAuth.getInstance().currentUser?.uid}").collection("DailyTasks")

    var tasksData: MutableMap<String, Int> = hashMapOf()
    private var todaysAverage: Int = 0
    var allTasks: MutableList<DailyTasks> = mutableListOf()
    var dataChangeNotifier = MutableLiveData<Boolean>()
    var daysActive : Long = 0

    fun loadTodaysData() {

        val date = fetchFormattedDate()
        docRef.document("$date")
            .get().addOnSuccessListener { documentSnapshot ->
                documentSnapshot.data?.let {
                    tasksData = it.getValue("dailyTasks") as MutableMap<String, Int>
                    todaysAverage =
                        (documentSnapshot.data?.getValue("totalCompletion") as Long).toInt()
                }
            }
    }

    fun writeTodaysData(tasks: DailyTasks) {

        val date = fetchFormattedDate()
        tasksData[tasks.taskName] = tasks.progress
        val dayAverage = getAverage(tasksData.values)
        updateTasks(tasks)

        docRef.document("$date")
            .set(
                hashMapOf(
                    "dailyTasks" to tasksData,
                    "totalCompletion" to dayAverage,
                    "timeStamp" to tasks.timeStamp
                )
            ).addOnSuccessListener {
                docRef.document("$date")
                    .set(
                        hashMapOf(
                            "documentDate" to date
                        ),
                        SetOptions.merge()
                    )
                loadAllData()
            }
    }

    fun deleteTask(data: String) {

        tasksData.remove(data)
        val dayAverage = getAverage(tasksData.values)

        docRef.document(System.currentTimeMillis().convertToDashDate())
            .update(
                hashMapOf(
                    "dailyTasks" to tasksData
                ) as Map<String, Any>
            )
            .addOnSuccessListener {
                docRef.document(System.currentTimeMillis().convertToDashDate())
                    .update(
                        hashMapOf(
                            "totalCompletion" to dayAverage
                        ) as Map<String, Any>
                    )
                    .addOnSuccessListener {
                        loadAllData()
                    }
                dataChangeNotifier.value = true
            }
    }

    fun updateProgress(text: CharSequence, p0: Int) {
        tasksData[text.toString()] = p0
        val dayAverage = getAverage(tasksData.values)
        updateTasks(
            DailyTasks(text.toString(), p0)
        )

        docRef.document(System.currentTimeMillis().convertToDashDate())
            .set(
                hashMapOf(
                    "dailyTasks" to hashMapOf(
                        "$text" to p0
                    )
                ) as Map<String, Any>, SetOptions.merge()
            ).addOnSuccessListener {
                docRef.document(System.currentTimeMillis().convertToDashDate())
                    .update(
                        hashMapOf(
                            "totalCompletion" to dayAverage
                        ) as Map<String, Any>
                    )
                    .addOnSuccessListener {
                        loadAllData()
                    }
            }
    }

    fun loadAnalyticsData(){
        FirebaseFirestore.getInstance().collection("allUsers")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")
            .collection("ProfileAnalytics").document("ProfileData")
            .get().addOnSuccessListener {documentSnapshot ->
                documentSnapshot.get("daysActive")?.let {
                    daysActive = it as Long
                }

                Log.i("FIRE_BAE", "$daysActive")
            }
    }

    fun loadAllData(startAct: () -> Unit = fun() {}) {
        var counter = 0
        allTasks.clear()
        docRef.get().addOnSuccessListener { querySnapshot ->
            querySnapshot.documents.forEach { docSnap ->
                docSnap.data?.let {
                    allTasks.add(
                        DailyTasks(
                            taskName = (it.getValue("timeStamp") as Long).convertLongToDateString(),
                            timeStamp = it.getValue("timeStamp") as Long,
                            progress = (it.getValue("totalCompletion") as Long).toInt()
                        )
                    )
                }
                counter++
            }
            if(counter!= daysActive.toInt()){
                daysActive = counter.toLong()
                updateDaysActive()
            }
            startAct.invoke()
        }
    }

    private fun updateDaysActive() {
        FirebaseFirestore.getInstance().collection("allUsers")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")
            .collection("ProfileAnalytics").document("ProfileData")
            .set(
                hashMapOf(
                    "daysActive" to daysActive
                ) as Map<String, Any>
            )
    }

    private fun updateTasks(tasks: DailyTasks) {
        tasksData[tasks.taskName] = tasks.progress
    }
}



