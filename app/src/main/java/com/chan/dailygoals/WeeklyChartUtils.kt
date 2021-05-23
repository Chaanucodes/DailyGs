package com.chan.dailygoals

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.EntryXComparator
import java.util.*

object ToDateFormat : ValueFormatter(){


    override fun getFormattedValue(value : Float ) : String
    {
        val splitValue = value.toString().split(".")
        return splitValue[0] +" "+when(splitValue[1]){
            "01" -> "Jan"
            "02" -> "Feb"
            "03" -> "Mar"
            "04" -> "Apr"
            "05" -> "May"
            "06" -> "Jun"
            "07" -> "Jul"
            "08" -> "Aug"
            "09" -> "Sep"
            "10" -> "Oct"
            "11" -> "Nov"
            "12" -> "Dec"

            else -> splitValue[1]
        }
//            return value.toString()
    }

}

fun setBar(
    lineChartWeeklyTimeRecord: LineChart,
    dataVals: HashMap<Float, Float>,
    context: Context
){
    lineChartWeeklyTimeRecord.setViewPortOffsets(60F, 20f, 60f, 80f)
    lineChartWeeklyTimeRecord.setBackgroundColor(Color.rgb(104, 241, 175))

    // no description text
    lineChartWeeklyTimeRecord.description.isEnabled = true
    lineChartWeeklyTimeRecord.description.text = "Weekly data"
    lineChartWeeklyTimeRecord.description.textSize = 16f
    lineChartWeeklyTimeRecord.description.textColor = context.getColor(R.color.white)

    // enable touch gestures

    // enable touch gestures
    lineChartWeeklyTimeRecord.setTouchEnabled(false)


    // enable scaling and dragging

    // enable scaling and dragging
    lineChartWeeklyTimeRecord.isDragEnabled = true
    lineChartWeeklyTimeRecord.setScaleEnabled(true)

    // if disabled, scaling can be done on x- and y-axis separately

    // if disabled, scaling can be done on x- and y-axis separately
    lineChartWeeklyTimeRecord.setPinchZoom(true)
    lineChartWeeklyTimeRecord.setBackgroundColor(context.getColor(R.color.transparent_med))
    lineChartWeeklyTimeRecord.setDrawGridBackground(false)
    lineChartWeeklyTimeRecord.maxHighlightDistance = 300F

    val x: XAxis = lineChartWeeklyTimeRecord.xAxis
    x.isEnabled = true
    x.position = XAxis.XAxisPosition.BOTTOM
    x.setDrawGridLines(false)
    x.axisLineColor = Color.WHITE
    x.textColor = Color.WHITE
    x.setLabelCount(7, true)
    x.setDrawLabels(true)
    x.valueFormatter = ToDateFormat


    val y: YAxis = lineChartWeeklyTimeRecord.axisLeft
    y.typeface = Typeface.DEFAULT
    y.setLabelCount(7, false)
    y.textColor = Color.WHITE
    y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
    y.setDrawGridLines(false)
    y.axisLineColor = Color.WHITE
    y.valueFormatter  = PercentFormatter()


    lineChartWeeklyTimeRecord.axisRight.isEnabled = false

    // add data

    // add data


    // lower max, as cubic runs significantly slower than linear


    lineChartWeeklyTimeRecord.legend.isEnabled = false

    lineChartWeeklyTimeRecord.animateXY(1500, 1500)

    // don't forget to refresh the drawing

    // don't forget to refresh the drawing
    lineChartWeeklyTimeRecord.invalidate()

    setData(dataVals, lineChartWeeklyTimeRecord, context)
}



private fun setData(
    dataVals: HashMap<Float, Float>,
    lineChartWeeklyTimeRecord: LineChart,
    context: Context
) {
    val values: ArrayList<Entry> = ArrayList()

    dataVals.forEach{
        values.add(Entry(it.key, it.value))
    }
    Collections.sort(values, EntryXComparator())
    val set1: LineDataSet
    if (lineChartWeeklyTimeRecord.data != null &&
        lineChartWeeklyTimeRecord.data.dataSetCount > 0
    ) {
        set1 = lineChartWeeklyTimeRecord.data.getDataSetByIndex(0) as LineDataSet
        set1.values = values
        lineChartWeeklyTimeRecord.data.notifyDataChanged()
        lineChartWeeklyTimeRecord.notifyDataSetChanged()
    } else {
        // create a dataset and give it a type
        set1 = LineDataSet(values, "Weekly data")
        set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        set1.cubicIntensity = 0.2f
        set1.setDrawFilled(true)
        set1.setDrawCircles(false)
        set1.lineWidth = 1.8f
        set1.circleRadius = 4f
        set1.setCircleColor(Color.WHITE)
        set1.highLightColor = context.getColor(R.color.colorPrimaryDark)
        set1.color = Color.WHITE
        set1.fillColor = Color.WHITE
        set1.fillAlpha = 100
        set1.setDrawHorizontalHighlightIndicator(false)
        set1.fillFormatter =
            IFillFormatter { _, _ ->
//                    lineChartWeeklyTimeRecord.axisLeft.axisMinimum
                lineChartWeeklyTimeRecord.xAxis.axisMinimum
            }

        // create a data object with the data sets
        val data = LineData(set1)
        data.setValueTypeface(Typeface.DEFAULT)
        data.setValueTextSize(9f)
        data.setDrawValues(false)

        // set data
        lineChartWeeklyTimeRecord.data = data
        lineChartWeeklyTimeRecord.refreshDrawableState()
    }
}
