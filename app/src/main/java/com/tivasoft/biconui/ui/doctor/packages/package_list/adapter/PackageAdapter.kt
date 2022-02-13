package com.tivasoft.biconui.ui.doctor.packages.package_list.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.BR
import com.tivasoft.biconui.data.model.network.responses.common.PackageData
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.ItemPackageBinding
import com.tivasoft.biconui.utils.PackageItemListener


class PackageAdapter(private val isPatient: Boolean) :
    RecyclerView.Adapter<PackageAdapter.PackageListViewHolder>() {
    private val list = ArrayList<PackageData>()
    private lateinit var listener: PackageItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPackageBinding.inflate(inflater, parent, false)
        return PackageListViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: PackageListViewHolder, position: Int) {
        val item = getItem(position)
        val constraintItems = holder.itemView.rootView.findViewById<LinearLayout>(R.id.cl_items)

        if (constraintItems.size == 0) {
            val dim = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            item.items.map {
                val textView = TextView(holder.itemView.context)
                textView.text = "\u25CF ${it.title}"
                textView.layoutParams = dim
                textView.setTextColor(R.color.profile_background)
                constraintItems.addView(textView)
            }
        }
        holder.bind(item)
    }

    private fun getItem(position: Int): PackageData = list[position]

    fun updateItems(items: ArrayList<PackageData>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnActivationClickListener(listener: PackageItemListener) {
        this.listener = listener
    }

    private fun updateItem(position: Int) {
        val item = getItem(position)
        item.active = !item.active
        list[position] = item
        notifyItemChanged(position)
    }

    inner class PackageListViewHolder(private val binding: ItemPackageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(packages: PackageData) {
            binding.apply {
                setVariable(BR.packages, packages)
                activation.setOnClickListener {
                    listener.onActivationClickListener(packages.id)
                    updateItem(bindingAdapterPosition)
                }
                if(isPatient)
                    activation.visibility = View.GONE
            }
        }
    }
}