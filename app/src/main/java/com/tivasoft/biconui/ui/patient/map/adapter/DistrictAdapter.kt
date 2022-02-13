package com.tivasoft.biconui.ui.patient.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.district.DistrictItem
import com.tivasoft.biconui.databinding.ItemDistrictBinding
import com.tivasoft.biconui.utils.DistrictItemListener

class DistrictAdapter(private val onClick: DistrictItemListener):RecyclerView.Adapter<DistrictAdapter.MyViewHolder>() {
    private var districts= ArrayList<DistrictItem>()


    inner class MyViewHolder(private val binding: ItemDistrictBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(district: DistrictItem, index: Int, action: DistrictItemListener) {
            binding.apply {
                tvDistrictName.text = district.name
                itemView.setOnClickListener{
                    action.onDistrictsAction(district)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDistrictBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return districts.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position,onClick)
    }

    private fun getItem(position: Int): DistrictItem = districts[position]


    fun updateItems(districts: ArrayList<DistrictItem>){
        this.districts.clear()
        this.districts = districts
        notifyDataSetChanged()
    }


}
