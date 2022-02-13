package com.tivasoft.biconui.utils

import com.tivasoft.biconui.data.model.local.database.ChatMessageCacheEntity

sealed class ConversationUiModel {
    data class ChatMessageItem(val entity: ChatMessageCacheEntity) : ConversationUiModel()
    data class SeparatorItem(val entity: ChatMessageCacheEntity) : ConversationUiModel()
}