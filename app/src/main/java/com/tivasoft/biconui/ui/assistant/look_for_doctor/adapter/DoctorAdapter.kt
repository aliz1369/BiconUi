package com.tivasoft.biconui.ui.assistant.look_for_doctor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.data.model.network.responses.assistant.DoctorByAssistanceData
import com.tivasoft.biconui.databinding.ItemDoctorByAssistanceBinding
import com.tivasoft.biconui.utils.AssistanceListener
import com.tivasoft.biconui.BR

class DoctorAdapter : RecyclerView.Adapter<DoctorAdapter.DoctorListViewHolder>() {

    private val list = ArrayList<DoctorByAssistanceData>()
    private lateinit var listener: AssistanceListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDoctorByAssistanceBinding.inflate(inflater, parent, false)
        return DoctorListViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: DoctorListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun updateItems(items: ArrayList<DoctorByAssistanceData>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): DoctorByAssistanceData = list[position]

    fun setOnAssistanceListener(assistanceListener: AssistanceListener) {
        listener = assistanceListener
    }

    inner class DoctorListViewHolder(private val binding: ItemDoctorByAssistanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: DoctorByAssistanceData) {
            binding.apply {
                setVariable(BR.doctor, doctor)
                assistanceAccept.setOnClickListener {
                    listener.onAccepted(doctor.id)
                }
                assistanceReject.setOnClickListener {
                    listener.onRejected(doctor.id)
                }
            }
        }
    }
}