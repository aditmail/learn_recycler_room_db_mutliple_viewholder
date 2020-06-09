/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        val application = this.activity!!.application

        val dataSource = SleepDatabase.getInstance(application) //Define/Init SleepDatabase
                .sleepDatabaseDao //and Add Interface since the class is abstract?

        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application) //Init ModelFactory
        val sleepTrackerViewModel = ViewModelProviders.of(this, viewModelFactory) //Init ViewModel and insert modelfactory as parameter
                .get(SleepTrackerViewModel::class.java)

        binding.sleepTrackerViewModel = sleepTrackerViewModel
        binding.setLifecycleOwner(this)

        /** Setting GridLayoutManager and Adapter for Recycler **/
        val gridManager = GridLayoutManager(activity, 3)
        gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> 3 //0 IS FOR Header ViewHolder.. 3 means the size to span since it in grid layout
                    else -> 1 //1 Is for List ViewHolder
                }
            }

        }
        binding.sleepList.layoutManager = gridManager

        //val adapter = SleepNightAdapter() //Before Adding OnClickListener
        val adapter = SleepNightAdapter(SleepNightListener { nightID ->
            Toast.makeText(context, "NightID: $nightID", Toast.LENGTH_SHORT).show()
            sleepTrackerViewModel.onSleepNightClick(nightID) //Go To Another Fragment
        }) //After Adding OnClickListener

        binding.sleepList.adapter = adapter
        /** Setting GridLayoutManager and Adapter for Recycler **/

        sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer {
            it?.let {
                //adapter.data = it //Adding Data to Adapter
                //adapter.submitList(it) //The Using of DiffUtil in Adapter
                adapter.addHeaderAndSubmitList(it) //The Using of DiffUtil in Adapter
            }
        })

        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer { navigate ->
            navigate?.let {
                //Move to FragmentQuality with Carry on Night ID Data
                this.findNavController().navigate(SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(navigate.nightID))
                sleepTrackerViewModel.doneNavigating() //Calling the done navigating also
            }
        })

        //Navigate and Pass Argmn to SleepDetailFragment
        sleepTrackerViewModel.navigateToSleepDetail.observe(viewLifecycleOwner, Observer { nightID ->
            nightID?.let {
                this.findNavController().navigate(SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepDetailFragment(nightID))
                sleepTrackerViewModel.doneNavigatingToSleepDetail()
            }
        })

        sleepTrackerViewModel.showSnackarEnvt.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Snackbar.make(
                        activity!!.findViewById(android.R.id.content),
                        getString(R.string.cleared_message),
                        Snackbar.LENGTH_SHORT
                ).show()

                sleepTrackerViewModel.doneShowingSnackbar()
            }
        })

        return binding.root
    }
}
