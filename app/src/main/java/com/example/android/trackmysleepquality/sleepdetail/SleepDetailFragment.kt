package com.example.android.trackmysleepquality.sleepdetail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepDetailBinding

class SleepDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentSleepDetailBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_detail, container, false)

        val argument = SleepDetailFragmentArgs.fromBundle(arguments!!)

        val application = this.activity!!.application
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        val viewModelFactory = SleepDetailViewModelFactory(argument.sleepNightKey, dataSource)
        val sleepDetailViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SleepDetailViewModel::class.java)

        binding.sleepDetail = sleepDetailViewModel
        binding.setLifecycleOwner(this)

        sleepDetailViewModel.navigateToSleepTracker.observe(this, Observer { navigate ->
            navigate?.let {
                this.findNavController().navigate(SleepDetailFragmentDirections
                        .actionSleepDetailFragmentToSleepTrackerFragment())
                sleepDetailViewModel.doneNavigating()
            }
        })

        /** My Type of Implemented Model.. from What I Learned **/
        /*sleepDetailViewModel.getTonightData.observe(viewLifecycleOwner, Observer { nights ->
            binding.imgQuality.setImageResource(
                    when (nights.sleepQuality) {
                        0 -> R.drawable.ic_sleep_0
                        1 -> R.drawable.ic_sleep_1
                        2 -> R.drawable.ic_sleep_2
                        3 -> R.drawable.ic_sleep_3
                        4 -> R.drawable.ic_sleep_4
                        5 -> R.drawable.ic_sleep_5
                        else -> R.drawable.ic_launcher_sleep_tracker_background
                    }
            )

            binding.tvStartTime.text = convertDurationToFormatted(nights.startTimeMill, nights.endTimeMill, context!!.resources)
        })*/
        /** My Type of Implemented Model.. from What I Learned **/

        return binding.root
    }
}
