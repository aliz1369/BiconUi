package com.tivasoft.biconui.ui.patient.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.BR
import com.tivasoft.biconui.data.model.network.responses.auth.SearchData
import com.tivasoft.biconui.databinding.ItemSearchBinding
import com.tivasoft.biconui.utils.SearchedItemListener

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    private val searchData = ArrayList<SearchData>()
    private lateinit var onClick: SearchedItemListener

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBinding.inflate(inflater, parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = getItems(position)
        holder.bind(item)
    }

    object DiffCallback : DiffUtil.ItemCallback<SearchData>() {
        override fun areItemsTheSame(
            oldItem: SearchData, newItem: SearchData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SearchData, newItem: SearchData
        ): Boolean {
            return oldItem == newItem
        }
    }

    private fun getItems(position: Int): SearchData = searchData[position]

    fun updateItems(searchData: ArrayList<SearchData>) {
        this.searchData.clear()
        this.searchData.addAll(searchData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return searchData.size
    }

    fun setOnItemClickListener(listener: SearchedItemListener) {
        onClick = listener
    }

    private fun updateItem(position: Int) {
        val item = getItems(position)
        item.isBooking = true
        searchData[position] = item
        notifyItemChanged(position)
    }

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(search: SearchData) {
            binding.apply {
                setVariable(BR.search, search)
                book.setOnClickListener {
                    onClick.onBookAction(search)
                    updateItem(bindingAdapterPosition)
                }
                packages.setOnClickListener {
                    onClick.onPackageClicked(search.id)
                }
            }
        }
    }
}