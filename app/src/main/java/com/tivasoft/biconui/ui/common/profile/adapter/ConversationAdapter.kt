package com.tivasoft.biconui.ui.common.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tivasoft.biconui.data.mapper.ChatCacheMapper
import com.tivasoft.biconui.data.model.local.database.ChatMessageCacheEntity
import com.tivasoft.biconui.data.model.local.ui.chat.SocketMessageType
import com.tivasoft.biconui.utils.ConversationUiModel
import com.tivasoft.biconui.data.model.local.ui.Message
import com.tivasoft.biconui.data.model.local.ui.MessageType
import com.tivasoft.biconui.databinding.*
import com.tivasoft.biconui.utils.ChatItemClickListener
import com.tivasoft.biconui.BR

class ConversationAdapter(
    private val isPatient: Boolean = true,
    userId: String
) : PagingDataAdapter<ConversationUiModel, ConversationViewHolder>(DiffCallback) {

    private val mapper = ChatCacheMapper(userId)
    private lateinit var listener: ChatItemClickListener

    override fun getItemViewType(position: Int): Int {
        val rawItem = getItemEntity(position)
        if (rawItem != null) {
            val item = mapper.mapFromEntity(rawItem)
            return when (item.messageType) {
                MessageType.OUTGOING -> {
                    when (item.contentType) {
                        SocketMessageType.TEXT -> 1
                        SocketMessageType.FILE -> 3
                        SocketMessageType.AUDIO -> 5
                        SocketMessageType.IMAGE -> 7
                        SocketMessageType.VIDEO -> 9
                        SocketMessageType.GAME -> 11
                        SocketMessageType.TEST -> 13
                        SocketMessageType.BREATH -> 15
                        SocketMessageType.NOT_SUPPORTED -> -1
                        SocketMessageType.DATE -> 0
                        SocketMessageType.UPLOADING -> 17
                    }
                }
                MessageType.INCOMING -> when (item.contentType) {
                    SocketMessageType.TEXT -> 2
                    SocketMessageType.FILE -> 4
                    SocketMessageType.AUDIO -> 6
                    SocketMessageType.IMAGE -> 8
                    SocketMessageType.VIDEO -> 10
                    SocketMessageType.GAME -> 12
                    SocketMessageType.TEST -> 14
                    SocketMessageType.BREATH -> 16
                    SocketMessageType.NOT_SUPPORTED -> -2
                    SocketMessageType.DATE -> 0
                    SocketMessageType.UPLOADING -> 17
                }
            }
        }
        return -3
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ConversationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            0 -> ItemChatTimeBinding.inflate(inflater, parent, false)
            1 -> when (isPatient) {
                true -> ItemChatBubbleOutgoingBinding.inflate(inflater, parent, false)
                false -> ItemChatBubbleOutgoingDoctorBinding.inflate(inflater, parent, false)
            }
            2 -> ItemChatBubbleIncomingBinding.inflate(inflater, parent, false)
            3 -> ItemChatOutgoingFileBinding.inflate(inflater, parent, false)
            4 -> ItemChatIncomingFileBinding.inflate(inflater, parent, false)
            5 -> ItemChatOutgoingVoiceBinding.inflate(inflater, parent, false)
            6 -> ItemChatIncomingVoiceBinding.inflate(inflater, parent, false)
            7 -> ItemChatOutgoingImageBinding.inflate(inflater, parent, false)
            8 -> ItemChatIncomingImageBinding.inflate(inflater, parent, false)
            9 -> ItemChatOutgoingVideoBinding.inflate(inflater, parent, false)
            10 -> ItemChatIncomingVideoBinding.inflate(inflater, parent, false)
            11, 13, 15 -> ItemChatOutgoingPrescriptionBinding.inflate(inflater, parent, false)
            12, 14, 16 -> ItemChatIncomingPrescriptionBinding.inflate(inflater, parent, false)
            17 -> ItemChatUploadingBinding.inflate(inflater, parent, false)
            else -> ItemChatIncomingNotSupportedBinding.inflate(inflater, parent, false)
        }
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ConversationViewHolder, position: Int
    ) {
        val rawItem = getItemEntity(position)
        if (rawItem != null) {
            val item = mapper.mapFromEntity(rawItem)
            holder.bind(item)
            holder.itemView.setOnClickListener {
                listener.onItemClickListener(item)
            }
        }
    }

    fun setOnItemClickListener(clickListener: ChatItemClickListener) {
        listener = clickListener
    }

    private fun getItemEntity(position: Int): ChatMessageCacheEntity? {
        return when (val uiItem = getItem(position)) {
            is ConversationUiModel.ChatMessageItem -> uiItem.entity
            is ConversationUiModel.SeparatorItem -> uiItem.entity
            else -> null
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ConversationUiModel>() {
        override fun areItemsTheSame(
            oldItem: ConversationUiModel, newItem: ConversationUiModel
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ConversationUiModel, newItem: ConversationUiModel
        ): Boolean = (oldItem is ConversationUiModel.ChatMessageItem &&
                newItem is ConversationUiModel.ChatMessageItem &&
                oldItem.entity._id == newItem.entity._id)
    }
}

class ConversationViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(messages: Message) {
        binding.setVariable(BR.message, messages)
    }
}