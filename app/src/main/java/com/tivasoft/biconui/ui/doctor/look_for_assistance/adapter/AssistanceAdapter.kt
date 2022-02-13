package com.tivasoft.biconui.ui.doctor.look_for_assistance.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.data.model.network.responses.doctor.AssistanceData
import com.tivasoft.biconui.databinding.ItemAssistanceBinding
import com.tivasoft.biconui.utils.AssistanceListener
import com.tivasoft.biconui.BR

class AssistanceAdapter : RecyclerView.Adapter<AssistanceAdapter.AssistanceListViewHolder>() {

    private val list = ArrayList<AssistanceData>()
    private lateinit var listener: AssistanceListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssistanceListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAssistanceBinding.inflate(inflater, parent, false)
        return AssistanceListViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: AssistanceListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun updateItems(items: ArrayList<AssistanceData>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): AssistanceData = list[position]

    fun setOnAssistanceListener(assistanceListener: AssistanceListener) {
        listener = assistanceListener
    }

    inner class AssistanceListViewHolder(private val binding: ItemAssistanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(assistance: AssistanceData) {
            binding.apply {
                setVariable(BR.assistance, assistance)
                assistanceAccept.setOnClickListener {
                    listener.onAccepted(assistance.id)
                }
                assistanceReject.setOnClickListener {
                    listener.onRejected(assistance.id)
                }
                root.setOnClickListener {
                    listener.onClicked(assistance)
                }
            }
        }
    }
}