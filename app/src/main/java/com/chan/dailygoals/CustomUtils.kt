package com.chan.dailygoals

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.text.format.DateFormat
import android.view.View
import android.widget.ScrollView
import com.chan.dailygoals.firecloud.FirebaseCustomManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

fun fetchFormattedDate(): String {
    val calendar= Calendar.getInstance();
    val dateFormat = SimpleDateFormat("dd-MM-yyyy");
    val date = dateFormat.format(calendar.getTime());
    return date
}

fun getAverage(list: MutableCollection<Int>) : Int{
    var avg = 0
    var counter = 0

    list.forEach {
        avg += it
        counter++
    }
    if(counter!= 0)
    avg /= counter

    return avg
}

fun Long.convertLongToDateString(): String {
    return SimpleDateFormat("EEEE MMM-dd-yyyy")
        .format(this).toString()
}

fun Long.convertToDashDate() : String{
    return SimpleDateFormat("dd-MM-yyyy")
        .format(this).toString()
}

fun checkDocRefNullability() {
    if(FirebaseCustomManager.docRef.path.contains("null") ){
        FirebaseCustomManager.docRef = FirebaseFirestore.getInstance().collection("allUsers")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")
            .collection("DailyTasks")
    }
    if(FirebaseCustomManager.profileRef.path.contains("null")){
        FirebaseCustomManager.profileRef = FirebaseFirestore.getInstance().collection("allUsers")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}").collection("ProfileAnalytics")
            .document("ProfileData")
    }

    if(FirebaseCustomManager.categoryRef.path.contains("null")){
        FirebaseCustomManager.categoryRef = FirebaseFirestore.getInstance().collection("allUsers")
            .document("${FirebaseAuth.getInstance().currentUser?.uid}")
            .collection("CustomizedTasks")
            .document("CustomTaskCategories")
    }
}

fun takeScreenShot(view: View, file: File): File {
    val date = Date()
    val format = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date)
    try {
        if (!file.exists()) {
            val mkdir = file.mkdir()
        }
        val path = "$file/DailyGoals-$format.jpeg"
//            view.setLayerType(LAYER_TYPE_SOFTWARE, Paint())
        val bitmap = Bitmap.createBitmap(
            (view as ScrollView).getChildAt(0)
                .width,
            (view as ScrollView).getChildAt(0).height,
            Bitmap.Config.ARGB_8888
        )
        val bgDrawable = view.background
        val canvas = Canvas(bitmap)
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
//            view.isDrawingCacheEnabled = false
        val imageFile = File(path)
        val fileOutputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
//        shareScreenShot(imageFile)
        return imageFile
    } catch (e: IOException) {

        val path = "$file/DailyGoals-$format.jpeg"
        return File(path)
    }
}


//For displaying PieChart data in UserStatsFragment
fun setDailyPieView(
    completed: Float,
    total: Float,
    dataName: String,
    pieChartRecord: PieChart,
    noDataText: String = "No data available"
) {
    val pieDataSet = PieDataSet(
        arrayListOf<PieEntry>(
            PieEntry(completed, " % Done"),
            PieEntry((total - completed), "% Not done   ")
        ), ""
    )
    pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
    pieDataSet.valueTextColor = Color.WHITE
    pieDataSet.valueTextSize = 18f

    pieChartRecord.apply {
        data = PieData(pieDataSet)
        description.isEnabled = false
        setUsePercentValues(true)
        isRotationEnabled = false
        centerText = dataName
        setCenterTextColor(resources.getColor(R.color.white))
        setCenterTextSize(22f)
        setNoDataText(noDataText)
//            isDrawHoleEnabled = false
        //transparent color
        holeRadius = 40.0f
//            setDrawSlicesUnderHole(true)
        setHoleColor(0x1000000)
        animate()
    }
}




