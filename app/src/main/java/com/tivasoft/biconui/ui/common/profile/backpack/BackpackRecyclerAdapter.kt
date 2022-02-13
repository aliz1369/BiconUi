package com.tivasoft.biconui.ui.common.profile.backpack

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.data.model.network.responses.common.Backpack
import com.tivasoft.biconui.databinding.ItemBackpackBinding
import com.tivasoft.biconui.utils.ListItemClickListener
import com.tivasoft.biconui.BR

class BackpackRecyclerAdapter : RecyclerView.Adapter<BackpackViewHolder>() {

    private val backpackItems: MutableList<Backpack> = mutableListOf()
    private lateinit var listener: ListItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackpackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBackpackBinding.inflate(inflater, parent, false)
        return BackpackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BackpackViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener.onItemClickListener(position)
        }
    }

    override fun getItemCount(): Int = backpackItems.size

    private fun getItem(position: Int) = backpackItems[position]

    fun updateItems(items: MutableList<Backpack>) {
        backpackItems.clear()
        backpackItems.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: ListItemClickListener) {
        this.listener = listener
    }
}

class BackpackViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(backpack: Backpack) {
        binding.setVariable(BR.backpack, backpack)
    }
}