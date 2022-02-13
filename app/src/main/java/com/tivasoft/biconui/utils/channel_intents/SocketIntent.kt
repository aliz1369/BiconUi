package com.tivasoft.biconui.utils.channel_intents

import com.tivasoft.biconui.data.model.local.ui.chat.SocketRoomInfo
import com.tivasoft.biconui.data.model.local.ui.chat.SocketMessage
import com.tivasoft.biconui.data.model.local.ui.chat.IsTyping
import com.tivasoft.biconui.data.model.local.ui.chat.SeenInfo
import com.tivasoft.biconui.data.model.network.responses.common.ChatMessage
import okhttp3.RequestBody

/**
 * Generic class for telling viewModel what should be done and with what parameters.
 */
sealed class SocketIntent {
    object ConnectToSocket : SocketIntent()
    object DisconnectFromSocket : SocketIntent()
    class JoinToRoom(val roomInfo: SocketRoomInfo) : SocketIntent()
    class LeaveFromRoom(val roomInfo: SocketRoomInfo) : SocketIntent()
    class SendMessage(val message: SocketMessage) : SocketIntent()
    class UserIsTyping(val isTyping: IsTyping) : SocketIntent()
    class UploadFile(val requestBody: RequestBody) : SocketIntent()
    class SeenMessages(val seenInfo: SeenInfo) : SocketIntent()
    class InsertPlaceholder(val message: ChatMessage) : SocketIntent()
    class RemovePlaceholder(val sender: String, val receiver: String) : SocketIntent()
}