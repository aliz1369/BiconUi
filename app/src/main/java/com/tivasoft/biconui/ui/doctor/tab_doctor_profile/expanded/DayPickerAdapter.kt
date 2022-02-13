package com.tivasoft.biconui.ui.doctor.tab_doctor_profile.expanded

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.data.model.local.ui.DayOfMoth
import com.tivasoft.biconui.databinding.ItemDayBinding
import com.tivasoft.biconui.utils.ListItemClickListener
import com.tivasoft.biconui.BR

class DayPickerAdapter : RecyclerView.Adapter<DayPickerViewHolder>() {
    private var list = ArrayList<DayOfMoth>()
    private lateinit var listener: ListItemClickListener
    private var oldPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayPickerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDayBinding.inflate(inflater, parent, false)
        return DayPickerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayPickerViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            if (oldPosition != -1) {
                val oldItem = getItem(oldPosition)
                oldItem.isSelected = false
                notifyItemChanged(oldPosition)
            }
            item.isSelected = true
            notifyItemChanged(position)
            oldPosition = holder.absoluteAdapterPosition
            listener.onItemClickListener(position)
        }
    }

    override fun getItemCount(): Int = list.size

    private fun getItem(position: Int): DayOfMoth = list[position]

    fun updateItems(list: ArrayList<DayOfMoth>) {
        oldPosition = list.indexOfFirst { it.isToday }
        this.list.clear()
        this.list = list
        notifyDataSetChanged()
    }

    fun setOnListItemClickListener(listener: ListItemClickListener) {
        this.listener = listener
    }
}

class DayPickerViewHolder(private val binding: ItemDayBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(dayOfMoth: DayOfMoth) {
        binding.setVariable(BR.dayOfMoth, dayOfMoth)
    }
}