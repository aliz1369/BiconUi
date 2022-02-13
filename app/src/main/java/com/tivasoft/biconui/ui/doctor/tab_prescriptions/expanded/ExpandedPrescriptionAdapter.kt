package com.tivasoft.biconui.ui.doctor.tab_prescriptions.expanded

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.BR
import com.tivasoft.biconui.data.model.network.responses.common.PrescriptionData
import com.tivasoft.biconui.databinding.ItemPrescriptionsBinding
import com.tivasoft.biconui.utils.ListItemClickListener

class ExpandedPrescriptionAdapter :
    PagingDataAdapter<PrescriptionData, PrescriptionViewHolder>(DiffCallback) {
    private val prescriptionList = ArrayList<PrescriptionData>()
    companion object {
        const val singleSpan = 1
        const val doubleSpan = 2
    }

    private var listener: ListItemClickListener? = null

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item?.type) {
            3 -> doubleSpan
            else -> singleSpan
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrescriptionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPrescriptionsBinding.inflate(inflater, parent, false)
        return PrescriptionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PrescriptionViewHolder, position: Int
    ) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
        holder.itemView.setOnClickListener {
            listener?.onItemClickListener(position)
        }
    }

    fun setOnListClickListener(listener: ListItemClickListener) {
        this.listener = listener
    }

    object DiffCallback : DiffUtil.ItemCallback<PrescriptionData>() {
        override fun areItemsTheSame(
            oldItem: PrescriptionData, newItem: PrescriptionData
        ): Boolean {
            return oldItem.data.id == newItem.data.id
        }

        override fun areContentsTheSame(
            oldItem: PrescriptionData, newItem: PrescriptionData
        ): Boolean {
            return oldItem == newItem
        }
    }

    fun updateItems(items: ArrayList<PrescriptionData>) {
        prescriptionList.clear()
        prescriptionList.addAll(items)
        notifyDataSetChanged()
    }
}

class PrescriptionViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(prescription: PrescriptionData) {
        binding.setVariable(BR.prescription, prescription)
    }
}