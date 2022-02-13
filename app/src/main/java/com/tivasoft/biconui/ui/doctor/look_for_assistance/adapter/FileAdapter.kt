package com.tivasoft.biconui.ui.doctor.look_for_assistance.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.databinding.ItemAttachmentPreviewBinding
import com.tivasoft.biconui.BR

class FileAdapter : RecyclerView.Adapter<FileAdapter.FileAdapterViewHolder>() {

    private val list = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAttachmentPreviewBinding.inflate(inflater, parent, false)
        return FileAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FileAdapterViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun updateItems(items: ArrayList<String>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): String = list[position]


    inner class FileAdapterViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            binding.setVariable(BR.url, url)
        }
    }
}