package com.tivasoft.biconui.data.mapper

import com.tivasoft.biconui.data.model.local.database.ChatMessageCacheEntity
import com.tivasoft.biconui.data.model.network.responses.common.ChatMessage
import com.tivasoft.biconui.utils.EntityMapper
import javax.inject.Inject

class ChatMapper @Inject constructor() : EntityMapper<ChatMessage, ChatMessageCacheEntity> {
    override fun mapFromEntity(entity: ChatMessage) =
        ChatMessageCacheEntity(
            time = entity.time,
            seen = entity.seen,
            _id = entity._id,
            type = entity.type,
            content = entity.content,
            sender = entity.sender,
            receiver = entity.receiver
        )

    override fun mapFromEntityList(entities: List<ChatMessage>): List<ChatMessageCacheEntity> =
        entities.map { mapFromEntity(it) }

    private fun mapToEntity(domainModel: ChatMessageCacheEntity) =
        ChatMessage(
            time = domainModel.time,
            seen = domainModel.seen,
            _id = domainModel._id,
            type = domainModel.type,
            content = domainModel.content,
            sender = domainModel.sender,
            receiver = domainModel.receiver
        )

    fun mapToEntityList(domainModels: List<ChatMessageCacheEntity>): List<ChatMessage> =
        domainModels.map { mapToEntity(it) }
}