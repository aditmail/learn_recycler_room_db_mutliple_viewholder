package com.example.android.trackmysleepquality.sleepdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

class SleepDetailViewModel(
        private val sleepNightKey: Long = 0L,
        val dataSource: SleepDatabaseDao) : ViewModel() {

    private val viewModelJob = Job()

    /** My Model **/
    /*private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _getTonightData = MutableLiveData<SleepNight>()
    val getTonightData: LiveData<SleepNight>
        get() = _getTonightData*/
    /** My Model **/

    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker


    /** Implemented from Udacity **/
    val database = dataSource

    private val night = MediatorLiveData<SleepNight>()
    fun getNight() = night //This Will be Called on XML
    //JUST THAT??? WOOW
    /** Implemented from Udacity **/

    init {
        viewTonightData()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onNavigateToTracker() {
        _navigateToSleepTracker.value = true
    }

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    private fun viewTonightData() {
        /** Implemented from Udacity **/
        night.addSource(database.getNightWithID(sleepNightKey), night::setValue)

        /** My Model **/
        /*uiScope.launch {
            _getTonightData.value = getTonightDataDB(sleepNightKey)
        }*/
    }

    /** My Model **/
    private suspend fun getTonightDataDB(sleepNightKey: Long): SleepNight? {
        return withContext(Dispatchers.IO) {
            val night = database.get(sleepNightKey)
            night
        }
    }
}
