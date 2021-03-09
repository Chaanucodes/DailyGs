package com.chan.dailygoals

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter

@SuppressLint("SetTextI18n")
@BindingAdapter("addPercentageSign")
fun TextView.addPercentageSign(percent : Int){
    this.text = "$percent%"
}
@BindingAdapter("addAveragePrefix")
fun TextView.addAveragePrefix(days : Int){
    this.text = "Total days active : $days"
}