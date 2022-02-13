package com.tivasoft.biconui.ui.common.profile.conversation

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.tivasoft.biconui.data.model.local.database.ChatMessageCacheEntity
import com.tivasoft.biconui.data.model.network.responses.common.SocketFilesResponse
import com.tivasoft.biconui.data.model.local.ui.chat.SocketMessageType
import com.tivasoft.biconui.data.model.network.responses.common.MeetResponse
import com.tivasoft.biconui.data.model.network.responses.tests.SingleTest
import com.tivasoft.biconui.data.source.*
import com.tivasoft.biconui.data.source.repositories.ConversationRepository
import com.tivasoft.biconui.data.source.repositories.MeetRepository
import com.tivasoft.biconui.data.source.repositories.UploadRepository
import com.tivasoft.biconui.utils.*
import com.tivasoft.biconui.utils.channel_intents.MeetIntent
import com.tivasoft.biconui.utils.channel_intents.SocketIntent
import com.google.gson.Gson
import com.tivasoft.biconui.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.Ack
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val socket: Socket,
    private val meetRepository: MeetRepository,
    private val uploadRepository: UploadRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {
    val channel = Channel<SocketIntent>(Channel.BUFFERED)
    val meetChannel = Channel<MeetIntent>(Channel.BUFFERED)

    private val _testItem = MutableStateFlow<DataState<SingleTest>>(DataState.Idle)
    val testItem: StateFlow<DataState<SingleTest>> get() = _testItem

    private val _meetItem = MutableStateFlow<DataState<MeetResponse>>(DataState.Idle)
    val meetItem: StateFlow<DataState<MeetResponse>> get() = _meetItem

    private val _file = MutableStateFlow<DataState<SocketFilesResponse>>(DataState.Idle)
    val file: StateFlow<DataState<SocketFilesResponse>> get() = _file

    init {
        handleIntent()
        handleMeetIntent()
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getConversations(
        firstUserId: String,
        secondUserId: String
    ): Flow<PagingData<ConversationUiModel>> = conversationRepository.getConversations(
        firstUserId, secondUserId
    ).flow.map { pagingData -> pagingData.map { ConversationUiModel.ChatMessageItem(it) } }
        .map {
            it.insertSeparators { after, before ->
                if (after == null)
                    return@insertSeparators null
                val entity = ChatMessageCacheEntity(
                    _id = System.currentTimeMillis().toString(),
                    type = SocketMessageType.DATE.type,
                    time = after.entity.time,
                    content = "",
                    seen = true,
                    sender = after.entity.sender,
                    receiver = after.entity.receiver
                )
                if (before == null)
                    return@insertSeparators ConversationUiModel.SeparatorItem(entity)
                val beforeDate = before.entity.time.substringBefore("T")
                val afterDate = after.entity.time.substringBefore("T")
                if (beforeDate != afterDate)
                    ConversationUiModel.SeparatorItem(entity)
                else
                    null
            }
        }.cachedIn(viewModelScope)

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { socketIntent ->
                when (socketIntent) {
                    SocketIntent.ConnectToSocket -> {
                        if (!socket.isActive) {
                            socket.on(Constants.SOCKET_CONNECTED, onConnect)
                            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
                            socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
                            socket.on(Constants.SOCKET_MESSAGE, onNewMessage)
                            socket.on(Constants.SOCKET_TYPING, onIsTyping)
                            socket.connect()
                        }
                    }
                    SocketIntent.DisconnectFromSocket -> {
                        socket.disconnect()
                        socket.off(Constants.SOCKET_CONNECTED, onConnect)
                        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
                        socket.off(Socket.EVENT_DISCONNECT, onDisconnect)
                        socket.off(Constants.SOCKET_MESSAGE, onNewMessage)
                        socket.off(Constants.SOCKET_TYPING, onIsTyping)
                    }
                    is SocketIntent.JoinToRoom -> {
                        val data = Gson().toJson(socketIntent.roomInfo)
                        socket.emit(Constants.SOCKET_JOIN, data, onUserJoined)
                    }
                    is SocketIntent.LeaveFromRoom -> {
                        socket.emit(Constants.SOCKET_LEAVE, socketIntent.roomInfo, onUserLeft)
                    }
                    is SocketIntent.SendMessage -> {
                        Log.e(
                            "SendMessage",
                            "sender:${socketIntent.message.sender}:${socketIntent.message.receiver}:${socketIntent.message.content}"
                        )
                        val data = Gson().toJson(socketIntent.message)
                        socket.emit(Constants.SOCKET_MESSAGE, data, onMessageSent)
                    }
                    is SocketIntent.UserIsTyping -> {
                        val data = Gson().toJson(socketIntent.isTyping)
                        socket.emit(Constants.SOCKET_TYPING, data)
                    }
                    is SocketIntent.UploadFile -> {
                        uploadRepository.uploadChatFile(socketIntent.requestBody)
                            .onEach { _file.value = it }
                            .launchIn(viewModelScope)
                    }
                    is SocketIntent.SeenMessages -> {
                        conversationRepository.setSeen(socketIntent.seenInfo)
                            .launchIn(viewModelScope)
                    }
                    is SocketIntent.InsertPlaceholder -> {
                        conversationRepository.addNewMessage(socketIntent.message)
                    }
                    is SocketIntent.RemovePlaceholder -> {
                        conversationRepository.removeMessage(
                            socketIntent.sender,
                            socketIntent.receiver
                        )
                    }
                }
            }
        }
    }

    private fun handleMeetIntent() {
        viewModelScope.launch {
            meetChannel.consumeAsFlow().collect { meetIntent ->
                when (meetIntent) {
                    is MeetIntent.Call -> meetRepository.call(meetIntent.entity)
                        .onEach { _meetItem.value = it }
                        .launchIn(viewModelScope)
                    is MeetIntent.EndCall -> meetRepository.endCall(meetIntent.entity)
                        .launchIn(viewModelScope)
                }
            }
        }
    }


    private val onConnect = Emitter.Listener { Log.e("TAG", "onConnect") }
    private val onConnectError = Emitter.Listener {
        it.map { error ->
            Log.e("onConnectError", error.toString())
        }
    }
    private val onDisconnect = Emitter.Listener { Log.e("TAG", "onDisconnect") }

    private val onUserJoined = Ack {
        it.map { user ->
            Log.e("onUserJoined", user.toString())
        }
    }
    private val onUserLeft = Ack {
        it.map { user ->
            Log.e("onUserLeft", user.toString())
        }
    }
    private val onMessageSent = Ack {
        it.map { message ->
            Log.e("onMessageSent", message.toString())
        }
    }
    private val onNewMessage = Emitter.Listener {
        viewModelScope.launch {
            it.map { message ->
                conversationRepository.addNewMessage(message)
                Log.e("onNewMessage", message.toString())
            }
        }
    }
    private val onIsTyping = Emitter.Listener {
        Log.e("TAG", "isTyping: ${it.size}")
    }

    override fun onCleared() {
        super.onCleared()
        socket.close()
    }
}