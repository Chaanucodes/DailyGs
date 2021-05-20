package com.chan.dailygoals.models

import android.os.Parcel
import android.os.Parcelable

data class DailyTasks(
    var taskName: String = "",
    var progress: Int = 0,
    var timeStamp: Long = System.currentTimeMillis(),
    var documentDate: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(taskName)
        parcel.writeInt(progress)
        parcel.writeLong(timeStamp)
        parcel.writeString(documentDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DailyTasks> {
        override fun createFromParcel(parcel: Parcel): DailyTasks {
            return DailyTasks(parcel)
        }

        override fun newArray(size: Int): Array<DailyTasks?> {
            return arrayOfNulls(size)
        }
    }

}


