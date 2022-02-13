package com.tivasoft.biconui.ui.patient.tab_doctor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.data.model.network.responses.doctor.SpecialtiesData
import com.tivasoft.biconui.BR
import com.tivasoft.biconui.databinding.ItemSpecialtiesBinding
import com.tivasoft.biconui.utils.SpecialtyItemListener

class SpecialtiesAdapter : RecyclerView.Adapter<SpecialtyViewHolder>() {

    private var specialties = ArrayList<SpecialtiesData>()
    private lateinit var onClick: SpecialtyItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialtyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSpecialtiesBinding.inflate(inflater, parent, false)
        return SpecialtyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpecialtyViewHolder, position: Int) {
        val item = getItem(position)
        holder.apply {
            bind(item)
            itemView.setOnClickListener {
                onClick.onSpecialtyAction(item)
            }
        }
    }

    override fun getItemCount(): Int = specialties.size

    fun updateItems(specialties: ArrayList<SpecialtiesData>) {
        this.specialties.clear()
        this.specialties.addAll(specialties)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): SpecialtiesData = specialties[position]

    fun setOnItemClickListener(listener: SpecialtyItemListener) {
        onClick = listener
    }
}

class SpecialtyViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(specialty: SpecialtiesData) {
        binding.setVariable(BR.specialty, specialty)
    }
}