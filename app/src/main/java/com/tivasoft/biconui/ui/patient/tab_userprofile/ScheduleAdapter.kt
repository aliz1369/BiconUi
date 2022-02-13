package com.tivasoft.biconui.ui.patient.tab_userprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.BR
import com.tivasoft.biconui.data.model.local.ui.ScheduleItem
import com.tivasoft.biconui.data.model.local.ui.ScheduleType
import com.tivasoft.biconui.databinding.ItemScheduleHeaderBinding
import com.tivasoft.biconui.databinding.ItemUserEmptyBinding
import com.tivasoft.biconui.databinding.ItemUserScheduleBinding

class ScheduleAdapter : RecyclerView.Adapter<SchedulerViewHolder>() {
    private val scheduleList = ArrayList<ScheduleItem>()

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).scheduleType) {
            ScheduleType.ITEM -> 0
            ScheduleType.EMPTY -> 1
            ScheduleType.HEADER -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedulerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            0 -> ItemUserScheduleBinding.inflate(inflater, parent, false)
            1 -> ItemUserEmptyBinding.inflate(inflater, parent, false)
            else -> ItemScheduleHeaderBinding.inflate(inflater, parent, false)
        }
        return SchedulerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SchedulerViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemCount(): Int = scheduleList.size

    private fun getItem(position: Int): ScheduleItem = scheduleList[position]

    fun updateItems(items: ArrayList<ScheduleItem>) {
        scheduleList.clear()
        scheduleList.addAll(items)
        notifyDataSetChanged()
    }
}

class SchedulerViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(scheduleItem: ScheduleItem) {
        binding.setVariable(BR.schedule, scheduleItem)
    }
}