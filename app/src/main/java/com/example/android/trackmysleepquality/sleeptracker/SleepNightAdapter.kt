package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightGridBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class SleepNightAdapter(val clickListener: SleepNightListener) :
//ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) { //Using a DiffUtil for The Data //Before Adding Header for the List Inside
        ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) { //Using a DiffUtil for The Data //After Adding Header for the List Inside
    //class SleepNightAdapter : RecyclerView.Adapter<SleepNightAdapter.ViewHolder>() { //Old Model Using RecyclerView.Adapter

    //Since Using DiffUtil.. This Doesnt Needed
    /*var data = listOf<SleepNight>() //Get List of Data from SleepNight
        //To let the Adapter if New Data... Have Changed. to Updating
        //If this Not Exist.. Data wont show on Recyclerview
        set(value) {
            field = value
            notifyDataSetChanged()
        }*/

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addHeaderAndSubmitList(list: List<SleepNight>) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map {
                    DataItem.SleepNightItem(it)
                }
            }

            withContext(Dispatchers.Main){
                submitList(items)
            }
        }
    }

    /** BeforeAddingLayoutHeader **/
    //override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown ViewType ${viewType}")
        }


        //return ViewHolder.from(parent) //Encapsulating..
        //Step..
        //1. Refactor the data to companion
        //2. Move the companion to Class ViewHolder
        //3. Adding constructor for ViewHolder class
        //4. Change the return with calling the ViewHolder.from ....
    }

    //This Doesnt Needed too for the DiffUtil because will auto calculate the data
    /*override fun getItemCount(): Int {
        return data.size
    }*/

    /** BeforeAddingLayoutHeader **/
    //override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        /** BeforeAddingLayoutHeader **/
        //val item = data[position] //Doesnt Need for the Using of DiffUtil

        //Using This
        //val item = getItem(position)

        //Before Implement OnClickListener
        //holder.bind(item) //Encapsulating the Data in ViewHolder.. //Cools

        //After Implement OnClickListener
        //holder.bind(clickListener, getItem(position)!!) //Encapsulating the Data in ViewHolder.. //Cools
        /** BeforeAddingLayoutHeader **/

        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(clickListener, nightItem.sleepNight)
            }
        }
    }

    //To Know What Type of Layout will be Shown
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class ViewHolder private constructor(val binding: ListItemSleepNightGridBinding) :
            RecyclerView.ViewHolder(binding.root) {

        //Before Binding
        /*private val sleepLength: TextView = binding.findViewById(R.id.tvSleepLength)
        private val quality: TextView = binding.findViewById(R.id.tvQuality)
        private val qualityImage: ImageView = binding.findViewById(R.id.imgQuality)*/

        fun bind(clickListener: SleepNightListener, item: SleepNight) {
            //val resImage = itemView.context.resources
            //val sleepQuality = item.sleepQuality

            //This is LinearLayoutManager Model
            //binding.sleepList = item //Binding from the XML Data equals SleepNight from Here
            //This is GridManager model
            binding.sleepListGrid = item //Binding from the XML Data equals SleepNight from Here

            binding.executePendingBindings() //Executing the BindingUtils..
            binding.clickListener = clickListener //Define the Binding Data

            //Unnedeed since the using of BindingUtils...
            /*//Inline... Refactor-> inline
            binding.tvQuality.text = convertNumericQualityToString(sleepQuality, resImage)
            binding.tvSleepLength.text = convertDurationToFormatted(item.startTimeMill, item.endTimeMill, resImage)
            binding.imgQuality.setImageResource(
                    when (sleepQuality) {
                        0 -> R.drawable.ic_sleep_0
                        1 -> R.drawable.ic_sleep_1
                        2 -> R.drawable.ic_sleep_2
                        3 -> R.drawable.ic_sleep_3
                        4 -> R.drawable.ic_sleep_4
                        5 -> R.drawable.ic_sleep_5
                        else -> R.drawable.ic_launcher_sleep_tracker_background
                    }
            )*/
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                //.inflate(R.layout.list_item_sleep_night, parent, false) //Using the DataBinding

                //val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false) //LinearModel
                val binding = ListItemSleepNightGridBinding.inflate(layoutInflater, parent, false) //GridModel
                return ViewHolder(binding)
            }
        }
    }
}

//For Header List..
class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun from(parent: ViewGroup): TextViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
                    .inflate(R.layout.header, parent, false)

            return TextViewHolder(layoutInflater)
        }
    }
}

//Diff Util will update data for the different data only..
//Which will not update all the data, but only the different one.. cools
//Since Using DataItem.. Change the SleepNight to it..
//class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {
class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        //return oldItem.nightID == newItem.nightID //Check if OldItem and New Item for NightID are Same...
        return oldItem.id == newItem.id //Check if OldItem and New Item for NightID are Same...
    }

    //Only called if areItemsTheSame returning True
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
        //return oldItem == newItem
    }
}

//Will Be Called if Click Happen...
class SleepNightListener(val clickListener: (sleepID: Long) -> Unit) {
    fun onClick(night: SleepNight) = clickListener(night.nightID)
}

//Setting for Adding Header and List for the RecyclerView..
sealed class DataItem {
    data class SleepNightItem(val sleepNight: SleepNight) : DataItem() {
        override val id = sleepNight.nightID
    }

    object Header : DataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}