package com.tivasoft.biconui.ui.common.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.data.model.network.responses.common.ConversationData
import com.tivasoft.biconui.databinding.ItemConversationBinding
import com.tivasoft.biconui.databinding.ItemConversationHeaderBinding
import com.tivasoft.biconui.utils.ConversationActionsListener
import com.tivasoft.biconui.BR

class ConversationListAdapter :
    RecyclerView.Adapter<ConversationListAdapter.ConversationListViewHolder>() {

    companion object {
        const val HEADER = 0
        const val ROW = 1
    }

    private val list = ArrayList<ConversationData>()
    private lateinit var listener: ConversationActionsListener

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.newMessageType == -1) {
            true -> HEADER
            false -> ROW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            HEADER -> ItemConversationHeaderBinding.inflate(inflater, parent, false)
            else -> ItemConversationBinding.inflate(inflater, parent, false)
        }
        return ConversationListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener.onItemClickListener(item.id, item.patientName)
        }
    }

    override fun getItemCount(): Int = list.size

    private fun getItem(position: Int): ConversationData = list[position]

    fun updateItems(items: ArrayList<ConversationData>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: ConversationActionsListener) {
        this.listener = listener
    }

    inner class ConversationListViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: ConversationData) {
            binding.setVariable(BR.conversation, conversation)
            if (binding is ItemConversationBinding) {
                binding.apply {
                    requestConfirm.setOnClickListener {
                        listener.onConfirmAction(conversation.id)
                    }
                    requestReject.setOnClickListener {
                        listener.onRejectAction(conversation.id)
                    }
                }
            }
        }
    }
}