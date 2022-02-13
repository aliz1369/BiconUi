package com.tivasoft.biconui.ui.doctor.tab_prescriptions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.BR
import com.tivasoft.biconui.data.model.network.responses.common.PrescriptionData
import com.tivasoft.biconui.data.model.network.responses.common.PrescriptionType
import com.tivasoft.biconui.databinding.ItemPrescriptionsBinding
import com.tivasoft.biconui.utils.PrescriptionListener

class PrescriptionsAdapter : RecyclerView.Adapter<PrescriptionViewHolder>() {
    private val prescriptionList = ArrayList<PrescriptionData>()

    private var listener: PrescriptionListener? = null

    companion object {
        const val singleSpan = 1
        const val doubleSpan = 2
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.type) {
            3 -> doubleSpan
            else -> singleSpan
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPrescriptionsBinding.inflate(inflater, parent, false)
        return PrescriptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val id = when (item.type) {
                PrescriptionType.GAME.value -> item.data.gameId!!
                else -> item.data.id
            }
            listener?.onPrescriptionItemClicked(item.type, id)
        }
    }

    override fun getItemCount(): Int = prescriptionList.size

    private fun getItem(position: Int): PrescriptionData = prescriptionList[position]

    fun updateItems(items: ArrayList<PrescriptionData>) {
        prescriptionList.clear()
        prescriptionList.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnListClickListener(listener: PrescriptionListener) {
        this.listener = listener
    }
}

class PrescriptionViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(prescription: PrescriptionData) {
        binding.setVariable(BR.prescription, prescription)
    }
}