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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao, //SleepDataInterface //and Application for accessing it
        application: Application) : AndroidViewModel(application) {

    /** Init Job and Coroutine Scope **/
    private var viewModelJob = Job() //Allow Cancel Coroutine if not used..
    //Scope to run on coroutine..
    //what is the job?
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob) //Dispatcher Main -> Run on Main Thread
    /** Init Job and Coroutine Scope **/

    /** Init Mutable and LiveData for List Data from DB **/
    private val _nights = database.getAllNight() //Get All Data From DB
    val nights: LiveData<List<SleepNight>>
        get() = _nights

    //TextView Model
    /*val nightString = Transformations.map(nights) { nightsData ->
        formatNights(nightsData, application.resources)
    }*/
    /** Init Mutable and LiveData for List Data from DB **/

    /** Init Mutable and LiveData to Direct to SleepQualityFrag **/
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality
    /** Init Mutable and LiveData to Direct to SleepQualityFrag **/

    /** Init Mutable and LiveData to Direct to SleepDetailFrag **/
    private val _navigateToSleepDetail = MutableLiveData<Long>()
    val navigateToSleepDetail: LiveData<Long>
        get() = _navigateToSleepDetail
    /** Init Mutable and LiveData to Direct to SleepDetailFrag **/

    /** Init Mutable and LiveData to Direct to ShowSnackbar (Boolean) **/
    private val _showSnackarEnvt = MutableLiveData<Boolean>()
    val showSnackarEnvt: LiveData<Boolean>
        get() = _showSnackarEnvt
    /** Init Mutable and LiveData to Direct to ShowSnackbar (Boolean) **/

    /** Init Mutable for Sleep Night DB **/
    private var _tonight = MutableLiveData<SleepNight>() //Observe the Data
    val tonight: LiveData<SleepNight>
        get() = _tonight

    /** Set Transformation Map for Visibility in Button **/
    val startBtnVisible = Transformations.map(_tonight) {
        it == null
        //If this click, the value aren't null
        //so the condition would not null == null (False)
        //So the button will be hide...

        //And the btnStop will visible because
        //arent' null != null (true)
    }

    val stopBtnVisible = Transformations.map(_tonight) {
        it != null
        //If this click, the value aren't null
        //But move to Sleep Quality and Back Here Again
        //The DB will call GetTonightFromDB to check if tonight data exist or Not
        //So the data will be null

        //so the condition would null != null (False)
        //So the button will be hide...
    }

    val clearBtnVisible = Transformations.map(_nights) {
        it?.isNotEmpty()
        //Since clearBtn click.. the tonight data will be Null
        //so It will check whether data is null or available in List Mode?
    }

    /** Set Transformation Map for Visibility in Button **/

    init {
        initializeTonight()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel() //Cancel All Coroutine onCleared
    }

    fun doneNavigating() {
        _navigateToSleepQuality.value = null //Clearing the Data
    }

    /** First Initialize to Get One Data From DB **/
    private fun initializeTonight() {
        uiScope.launch {
            //Based on the Scope that has been made, launch what?
            //Without blocking the main thread
            _tonight.value = getTonightFromDB()
        }
    }

    //Suspend because the calling inside the coroutine and not blocking main thread
    private suspend fun getTonightFromDB(): SleepNight? {
        //Calling with Coroutine too
        return withContext(Dispatchers.IO) {
            var night = database.getTonight() //get The Latest Data

            if (night?.endTimeMill != night?.startTimeMill) {
                night = null
            }

            night //return Night
        }
    }
    /** First Initialize to Get One Data From DB **/

    /** Starting Tracking -> Click for StartButton **/
    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            _tonight.value = getTonightFromDB()
        }
    }

    private suspend fun insert(newNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(newNight)
        }
    }
    /** Starting Tracking -> Click for StartButton **/

    /** Stop Tracking -> Click for StopButton **/
    fun onStopTracking() {
        uiScope.launch {
            val oldNight = _tonight.value ?: return@launch
            oldNight.endTimeMill = System.currentTimeMillis()
            update(oldNight)

            _navigateToSleepQuality.value = oldNight //Navigate to Sleep Frag with Latest Night Data to Pass
        }
    }

    fun onSleepNightClick(id: Long) {
        _navigateToSleepDetail.value = id
    }

    fun doneNavigatingToSleepDetail() {
        _navigateToSleepDetail.value = null
    }

    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }
    /** Stop Tracking -> Click for StopButton **/

    /** Clear All Tracking Data from DB-> Click for Clear Data **/
    fun onClear() {
        uiScope.launch {
            clear()
            _tonight.value = null
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }

        _showSnackarEnvt.value = true
    }

    /** Clear All Tracking Data from DB-> Click for Clear Data **/

    fun doneShowingSnackbar() {
        _showSnackarEnvt.value = false
    }
}

