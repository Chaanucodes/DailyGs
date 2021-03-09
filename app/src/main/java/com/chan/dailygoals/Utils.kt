package com.chan.dailygoals

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