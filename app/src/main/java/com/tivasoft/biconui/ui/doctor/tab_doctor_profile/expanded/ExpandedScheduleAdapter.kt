package com.tivasoft.biconui.ui.doctor.tab_doctor_profile.expanded

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
import com.tivasoft.biconui.utils.ListItemClickListener

class ExpandedScheduleAdapter :
    RecyclerView.Adapter<ExpandedSchedulerViewHolder>() {

    private var listener: ListItemClickListener? = null
    private val list = ArrayList<ScheduleItem>()

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).scheduleType) {
            ScheduleType.ITEM -> 0
            ScheduleType.EMPTY -> 1
            ScheduleType.HEADER -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandedSchedulerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            0 -> ItemUserScheduleBinding.inflate(inflater, parent, false)
            1 -> ItemUserEmptyBinding.inflate(inflater, parent, false)
            else -> ItemScheduleHeaderBinding.inflate(inflater, parent, false)
        }
        return ExpandedSchedulerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpandedSchedulerViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener?.onItemClickListener(position)
        }
    }

    override fun getItemCount(): Int = list.size

    fun setOnClickListener(listener: ListItemClickListener) {
        this.listener = listener
    }

    private fun getItem(index: Int) = list[index]

    fun updateItems(list: ArrayList<ScheduleItem>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}

class ExpandedSchedulerViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(scheduleItem: ScheduleItem) {
        binding.setVariable(BR.schedule, scheduleItem)
    }

}