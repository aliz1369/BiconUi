package com.tivasoft.biconui.ui.common.profile.add_schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.BR
import com.tivasoft.biconui.data.model.network.responses.doctor.AllPatientData
import com.tivasoft.biconui.databinding.ItemSchedulePatientListBinding
import com.tivasoft.biconui.utils.PatientItemListener

class PatientListAdapter(private val onclick: PatientItemListener) : RecyclerView.Adapter<PatientViewHolder>() {

    private val items = ArrayList<AllPatientData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSchedulePatientListBinding.inflate(inflater, parent, false)
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item,onclick)
        holder.itemView.setOnClickListener {
            onclick.onPatientAction(item)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun getItem(position: Int) = items[position]

    fun updateItems(items: ArrayList<AllPatientData>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
}

class PatientViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(patientData: AllPatientData, action: PatientItemListener) {
        binding.setVariable(BR.patient, patientData)
    }
}