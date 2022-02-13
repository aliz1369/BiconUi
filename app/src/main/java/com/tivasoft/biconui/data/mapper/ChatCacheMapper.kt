package com.tivasoft.biconui.data.mapper

import com.tivasoft.biconui.data.model.local.ui.Message
import com.tivasoft.biconui.data.model.local.ui.MessageType
import com.tivasoft.biconui.data.model.local.database.ChatMessageCacheEntity
import com.tivasoft.biconui.data.model.local.ui.chat.SocketMessageType
import com.tivasoft.biconui.utils.EntityMapper

class ChatCacheMapper(private val userId: String) : EntityMapper<ChatMessageCacheEntity, Message> {
    override fun mapFromEntity(entity: ChatMessageCacheEntity): Message =
        Message(
            id = entity._id,
            content = entity.content,
            time = entity.time,
            messageType = if (entity.sender == userId) MessageType.OUTGOING else MessageType.INCOMING,
            contentType = SocketMessageType.values()
                .firstOrNull { it.type.equals(entity.type, true) }
                ?: SocketMessageType.NOT_SUPPORTED,
            sender = entity.sender,
            receiver = entity.receiver
        )

    override fun mapFromEntityList(entities: List<ChatMessageCacheEntity>): List<Message> =
        entities.map { mapFromEntity(it) }
}