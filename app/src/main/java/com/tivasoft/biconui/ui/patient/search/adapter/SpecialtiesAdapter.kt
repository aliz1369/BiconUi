package com.tivasoft.biconui.ui.patient.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.BR
import com.tivasoft.biconui.data.model.network.responses.doctor.SpecialtiesData
import com.tivasoft.biconui.databinding.ItemSpecialtiesRadioBinding

class SpecialtiesAdapter : RecyclerView.Adapter<SpecialtiesAdapter.SpecialtiesViewHolder>() {

    private val specialtiesList = ArrayList<SpecialtiesData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialtiesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSpecialtiesRadioBinding.inflate(inflater, parent, false)
        return SpecialtiesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpecialtiesViewHolder, position: Int) {
        val items = getItem(position)
        holder.bind(items)
    }

    override fun getItemCount(): Int {
        return specialtiesList.size
    }

    private fun getItem(position: Int): SpecialtiesData = specialtiesList[position]

    fun clearCheck() {
        specialtiesList.mapIndexed { index, specialtiesData ->
            if (specialtiesData.isChecked) {
                specialtiesData.isChecked = false
                specialtiesList[index] = specialtiesData
                notifyItemChanged(index)
            }
        }
    }

    fun setChecked(position: Int) {
        clearCheck()
        val item = getItem(position)
        item.isChecked = !item.isChecked
        specialtiesList[position] = item
        notifyItemChanged(position)
    }

    fun updateItems(items: ArrayList<SpecialtiesData>) {
        this.specialtiesList.clear()
        this.specialtiesList.addAll(items)
        notifyDataSetChanged()
    }

    fun getSelectedId(): String = specialtiesList.firstOrNull { it.isChecked }?.id ?: ""

    inner class SpecialtiesViewHolder(private val binding: ItemSpecialtiesRadioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(specialties: SpecialtiesData) {
            binding.apply {
                setVariable(BR.specialties, specialties)
                rbSpecialties.setOnClickListener {
                    setChecked(bindingAdapterPosition)
                }
            }
        }
    }
}


