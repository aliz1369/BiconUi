package com.tivasoft.biconui.ui.doctor.packages.add_package.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.BR
import com.tivasoft.biconui.data.model.network.requests.doctor.ItemPackageData
import com.google.android.material.imageview.ShapeableImageView
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.ItemInPackageBinding
import com.tivasoft.biconui.utils.ItemPackageRemoveListener

class ItemInPackageAdapter(private val onClick: ItemPackageRemoveListener) :
    RecyclerView.Adapter<ItemInPackageListViewHolder>() {
    private val list = ArrayList<ItemPackageData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemInPackageListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemInPackageBinding.inflate(inflater, parent, false)
        return ItemInPackageListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemInPackageListViewHolder, position: Int) {
        val item = getItem(position)
        val remove = holder.itemView.findViewById<ShapeableImageView>(R.id.iv_remove)
        holder.bind(item)
        remove.setOnClickListener {
            onClick.onRemoveAction(item)
        }
    }

    private fun getItem(position: Int): ItemPackageData = list[position]

    fun updateItems(items: ArrayList<ItemPackageData>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun removeItems(item: ItemPackageData) {
        list.remove(item)
        notifyDataSetChanged()
    }
}

class ItemInPackageListViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(items: ItemPackageData) {
        binding.setVariable(BR.item, items)
    }
}