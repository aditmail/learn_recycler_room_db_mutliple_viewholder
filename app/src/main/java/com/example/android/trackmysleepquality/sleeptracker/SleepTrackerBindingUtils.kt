package com.example.android.trackmysleepquality.sleeptracker

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

//These Binding is Used for Binding the DataBinding...
//Which We always setting the logic for the list of data to show in the Recycler in the ViewHolder
//But with this, the Setting for the logic will be add here and let automate put the data in XML Layout of the List Item..
//Really Cools, everything seems easy.. but Who knows?

@BindingAdapter("sleepImage") //This will put in XML
fun ImageView.setSleepImage(item: SleepNight?) {
    item?.let {
        setImageResource(
                when (item.sleepQuality) {
                    0 -> R.drawable.ic_sleep_0
                    1 -> R.drawable.ic_sleep_1
                    2 -> R.drawable.ic_sleep_2
                    3 -> R.drawable.ic_sleep_3
                    4 -> R.drawable.ic_sleep_4
                    5 -> R.drawable.ic_sleep_5
                    else -> R.drawable.ic_launcher_sleep_tracker_background
                })
    }
}

@BindingAdapter("sleepDurationFormatted")
fun TextView.setSleepDurationFormatted(item: SleepNight?) { //Dont Forget to add ? to let it know if it null or not
    item?.let {
        text = convertDurationToFormatted(item.startTimeMill, item.endTimeMill, context.resources)
    }
}

@BindingAdapter("sleepQualityString")
fun TextView.setSleepQualityRating(item: SleepNight?) {
    item?.let {
        text = convertNumericQualityToString(item.sleepQuality, context.resources)
    }
}