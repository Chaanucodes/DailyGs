package com.chan.dailygoals.firecloud

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.chan.dailygoals.*
import com.chan.dailygoals.models.DailyTasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FirebaseCustomManager {

    var userName: String = ""
    var docRef = FirebaseFirestore.getInstance().collection("allUsers")
        .document("${FirebaseAuth.getInstance().currentUser?.uid}").collection("DailyTasks")
    var profileRef = FirebaseFirestore.getInstance().collection("allUsers")
        .document("${FirebaseAuth.getInstance().currentUser?.uid}").collection("ProfileAnalytics")
        .document("ProfileData")

    var categoryRef = FirebaseFirestore.getInstance().collection("allUsers")
        .document("${FirebaseAuth.getInstance().currentUser?.uid}")
        .collection("CustomizedTasks")
        .document("CustomTaskCategories")

    var tasksData: MutableMap<String, Int> = hashMapOf()
    private var todaysAverage: Int = 0
    var myTaskCategories = ArrayList<String>()
    var allTasks: MutableList<DailyTasks> = mutableListOf()
    var dataChangeNotifier = MutableLiveData<Boolean>()
    var daysActive: Long = 0
    var totalTasksToday = 0
    var totalCompletedTasksToday = 0
    var allTimeTasks = 0
    var allTimeCompletedTasks = 0
    var startingPoint: Int = 0

    fun loadTodaysData(from: String = "default", startAct: () -> Unit = fun() {}) {

        val date = fetchFormattedDate()
        checkDocRefNullability()
        docRef.document(date)
            .get().addOnSuccessListener { documentSnapshot ->
                documentSnapshot.data?.let {
                    try{
                        (it.getValue("dailyTasks") as MutableMap<String, Long>?)?.let { dt->
                            dt.forEach { map ->
                                tasksData[map.key] = map.value.toInt()
                            }
                        }
                        (documentSnapshot.data?.getValue("totalCompletion") as Long?)?.let {tc->
                            todaysAverage = tc.toInt()
                        }
                    }catch (e : NoSuchElementException){}
                    finally {
                        loadCustomizedTasks()
                    }
                }

                if (from == "tasksVModel") {
                    startAct.invoke()
                } else {
                    loadAllData(startAct)
                }
            }
    }


    private fun loadCustomizedTasks(){
        try {
            categoryRef.get()
                .addOnSuccessListener {qSnap->
                    qSnap.data?.let {
                        myTaskCategories = ((it["MyTasks"] as ArrayList<String>))
                        Log.i("FIREBASE_CUSTOM_list", myTaskCategories.toString())
                    }
                }
        }catch (e: NoSuchElementException){
            Log.e("FIREBASE_CUSTOM", e.toString())
        }
    }

    //Called from TitleFragment and TasksFragment
    fun writeTodaysData(task: DailyTasks, doAfterLoadAllData : () -> Unit  = fun(){}) {

        val date = fetchFormattedDate()
        tasksData[task.taskName] = task.progress
        val dayAverage = getAverage(tasksData.values)
        updateTasks(task)

        docRef.document(date)
            .set(
                hashMapOf(
                    "dailyTasks" to tasksData,
                    "totalCompletion" to dayAverage,
                    "timeStamp" to task.timeStamp,
                )
            ).addOnSuccessListener {
                if (allTasks.isNullOrEmpty() || allTasks[0].documentDate != date) {
                    profileRef.update(
                        "totalTasksToday", FieldValue.increment(1)
                    ).addOnSuccessListener {
                        if(task.progress == 100) {
                            profileRef.update(
                                "tasksCompletedToday", FieldValue.increment(1)
                            )
                        }
                        dateUpdateAndLoadAllData(doAfterLoadAllData)
                    }
                    updateDaysActive()
                    val timeS = System.currentTimeMillis()
                    allTasks.add(
                        0,
                        DailyTasks(
                            taskName = timeS.convertLongToDateString(),
                            timeStamp = System.currentTimeMillis(),
                            progress = dayAverage,
                            documentDate = timeS.convertToDashDate()
                        )
                    )

                }else{
                    dateUpdateAndLoadAllData(doAfterLoadAllData)
                }

            }
    }

    fun addToMyCategories(name : String){
        if(!myTaskCategories.contains(name.trim().toLowerCase().capitalize())){
            categoryRef
                .update("MyTasks", FieldValue.arrayUnion(name))
                .addOnSuccessListener {
                    myTaskCategories.add(name)
                }
        }
    }

    private fun dateUpdateAndLoadAllData(doAfterLoadAllData : () -> Unit  = fun(){}){
        docRef.document(fetchFormattedDate())
            .set(
                hashMapOf(
                    "documentDate" to fetchFormattedDate()
                ),
                SetOptions.merge()
            )
        loadAllData(doAfterLoadAllData)
    }

    fun deleteTask(data: String, forAllEntryDeletion: () -> Unit) {

        var containsCompleted = false
        if (tasksData[data]!!.toInt() == 100) {
            containsCompleted = true
        }
        tasksData.remove(data)
        val dayAverage = getAverage(tasksData.values)

        docRef.document(fetchFormattedDate())
            .update(
                hashMapOf(
                    "dailyTasks" to tasksData,
                    "totalCompletion" to dayAverage
                ) as Map<String, Any>
            )
            .addOnSuccessListener {
                profileRef.update(
                    "totalTasksToday", FieldValue.increment(-1)
                ).addOnSuccessListener {

                    if (containsCompleted) {
                        profileRef.update(
                            "tasksCompletedToday", FieldValue.increment(-1)
                        )
                    }
                    if (tasksData.isNullOrEmpty()) {
                        deleteDayEntry(forAllEntryDeletion)
                    } else {
                        loadAllData()
                        dataChangeNotifier.value = true
                    }
                }

            }
    }

    private fun deleteDayEntry(forAllEntryDeletion: () -> Unit) {
        docRef.document(fetchFormattedDate()).delete()
            .addOnSuccessListener {
                profileRef.update(
                    "daysActive", FieldValue.increment(-1)
                ).addOnSuccessListener {
                    totalCompletedTasksToday = 0
                    totalTasksToday = 0
                    allTasks.removeAt(0)
                    loadAllData(forAllEntryDeletion)
                }
            }
    }

    fun updateProgress(text: CharSequence, p0: Int) {
        tasksData[text.toString()] = p0
        val dayAverage = getAverage(tasksData.values)
        updateTasks(
            DailyTasks(text.toString(), p0)
        )

        docRef.document(fetchFormattedDate())
            .set(
                hashMapOf(
                    "dailyTasks" to hashMapOf(
                        "$text" to p0
                    )
                ) as Map<String, Any>, SetOptions.merge()
            ).addOnSuccessListener {
                docRef.document(fetchFormattedDate())
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

    fun loadDailyAnalytics(task: () -> Unit = fun() {}) {
        if (!tasksData.isNullOrEmpty() && !allTasks.isNullOrEmpty()
            && allTasks[0].documentDate.contains(fetchFormattedDate())
        ) {

            totalCompletedTasksToday = 0
            totalTasksToday = 0
            tasksData.forEach {
                if (it.value == 100) {
                    totalCompletedTasksToday++
                }
                totalTasksToday++
            }

        } else if (!allTasks.isNullOrEmpty()
            && !allTasks[0].documentDate.contains(fetchFormattedDate())
        ) {
            var ttt: Int
            var tct: Int
            profileRef.get().addOnSuccessListener { documentSnapshot ->
                documentSnapshot.data?.let { data ->
                    ttt = (data.getValue("totalTasksToday") as Long).toInt()
                    tct = (data.getValue("tasksCompletedToday") as Long).toInt()

                    if (ttt > 0) {
                        profileRef.update(
                            "allTimeTasks",
                            FieldValue.increment(ttt.toLong())
                        )
                            .addOnSuccessListener {
                                profileRef.update(
                                    "allTimeCompletedTasks",
                                    FieldValue.increment(tct.toLong())
                                )
                            }.addOnSuccessListener {
                                profileRef.update(
                                    hashMapOf(
                                        "totalTasksToday" to 0,
                                        "tasksCompletedToday" to 0
                                    ) as Map<String, Any>
                                ).addOnSuccessListener {
                                    totalCompletedTasksToday = 0
                                    totalTasksToday = 0
                                }
                            }
                    }
                }
            }
        }
        profileRef.get().addOnSuccessListener { documentSnapshot ->
            documentSnapshot.data?.let { data ->
                allTimeTasks = (data.getValue("allTimeTasks") as Long).toInt()
                allTimeCompletedTasks = (data.getValue("allTimeCompletedTasks") as Long).toInt()
                loadDaysActive { task() }
            }

        }.addOnFailureListener {
            Log.i("FIREBASE_CUSTOM", "analytics fail : ${it.message}")
        }
    }

    fun updateDailyAnalytics(tasksCompleted: Int, totalTasks: Int, incompleteTasks: Int) {
        docRef.document(fetchFormattedDate())
            .set(
                hashMapOf(
                    "tasksCompleted" to tasksCompleted,
                    "totalTasks" to totalTasks,
                    "incompleteTasks" to incompleteTasks
                ) as Map<String, Any>, SetOptions.merge()
            ).addOnSuccessListener {
                profileRef.set(
                    hashMapOf(
                        "totalTasksToday" to totalTasks,
                        "tasksCompletedToday" to tasksCompleted
                    ) as Map<String, Any>, SetOptions.merge()
                ).addOnSuccessListener {
                    totalCompletedTasksToday = tasksCompleted
                    totalTasksToday = totalTasks
                }
            }

    }


    fun loadAllData(startAct: () -> Unit = fun() {}, initialPoint: Int = 0) {
        checkDocRefNullability()
        val ref: Query
        if (initialPoint == 0) {
            ref = docRef.orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(30)

        } else {
            ref = docRef.orderBy("timeStamp", Query.Direction.DESCENDING)
                .startAfter(initialPoint)
                .limit(30)
            startingPoint = initialPoint
        }


        ref.get().addOnSuccessListener { docSnaphot ->
            if(initialPoint == 0)
            allTasks.clear()

            docSnaphot.documents.forEachIndexed { i, docSnap ->
                docSnap.data?.let { data ->
                    data.getValue("timeStamp")?.let {

                        //here taskName is actually date, this is for TitleFragment
                        allTasks.add(
                            DailyTasks(
                                taskName = (it as Long).convertLongToDateString(),
                                timeStamp = it,
                                progress = (data.getValue("totalCompletion") as Long).toInt(),
                                documentDate = it.convertToDashDate()
                            )
                        )
                    }
                }
            }

            if (allTasks.isNullOrEmpty()) setNewUserData()
            else loadDailyAnalytics()
            startAct.invoke()
        }
            .addOnFailureListener {
                Log.e("TAG_ALL_TASKS", it.toString())
            }

    }

    private fun setNewUserData() {
        daysActive = 0
        profileRef
            .set(
                hashMapOf(
                    "daysActive" to daysActive,
                    "totalTasksToday" to 0,
                    "tasksCompletedToday" to 0,
                    "allTimeTasks" to 0,
                    "allTimeCompletedTasks" to 0
                ) as Map<String, Any>
            ).addOnSuccessListener {
                categoryRef.set(
                    arrayListOf<String>()
                )
            }
    }

//    private fun isLatestEntryOfToday(date: String) {
//        if(date != fetchFormattedDate()){
//            updateDailyAnalytics(0,0,0)
//        }else{
//            aggregateTotalTasks()
//        }
//    }


    private fun updateDaysActive() {
        profileRef
            .update("daysActive", FieldValue.increment(1))
    }

    private fun updateTasks(tasks: DailyTasks) {
        tasksData[tasks.taskName] = tasks.progress
    }

    fun loadDaysActive(
        firstLogin: Boolean = false,
        startAct: () -> Unit = fun() {}
    ) {
        checkDocRefNullability()
        profileRef
            .get().addOnSuccessListener { documentSnapshot ->
                documentSnapshot.get("daysActive")?.let {
                    daysActive = it as Long
                }
                if (firstLogin) {
                    loadTodaysData(startAct = startAct)
                }else{
                    startAct.invoke()
                }
            }
    }

    fun passUsersName(
        firstLogin: Boolean = false,
        startAct: () -> Unit = fun() {}
    ) {
        FirebaseFirestore.getInstance().collection("allUsers")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")
            .set(
                hashMapOf(
                    "userName" to userName
                )
            ).addOnSuccessListener {
                if (firstLogin)
                    loadDaysActive(
                        firstLogin,
                        startAct
                    )
            }
    }

    fun loadUserName() {
        FirebaseFirestore.getInstance().collection("allUsers")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")
            .get().addOnSuccessListener { documentSnapshot ->
                documentSnapshot.get("userName")?.let {
                    userName = it as String
                }
            }
    }

    fun clearAll(logout: () -> Unit) {
        userName = ""
        docRef = FirebaseFirestore.getInstance().collection("null")
        profileRef = FirebaseFirestore.getInstance().collection("null").document("null")
        categoryRef = FirebaseFirestore.getInstance().collection("null").document("null")
        tasksData = hashMapOf()
        todaysAverage = 0
        allTasks = mutableListOf()
        dataChangeNotifier = MutableLiveData<Boolean>()
        daysActive = 0
        totalTasksToday = 0
        totalCompletedTasksToday = 0
        allTimeTasks = 0
        allTimeCompletedTasks = 0
        startingPoint = 0
        logout()
    }
}



