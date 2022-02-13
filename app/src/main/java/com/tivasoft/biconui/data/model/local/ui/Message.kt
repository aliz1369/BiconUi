package com.tivasoft.biconui.data.model.local.ui

import androidx.annotation.DrawableRes
import com.tivasoft.biconui.data.model.local.ui.chat.SocketMessageType

data class Message(
    val id: String,
    val content: String,
    val time: String,
    val messageType: MessageType,
    val contentType: SocketMessageType,
    @DrawableRes val icon: Int = 0,
    val sender: String,
    val receiver: String
)

enum class MessageType {
    INCOMING,
    OUTGOING
}