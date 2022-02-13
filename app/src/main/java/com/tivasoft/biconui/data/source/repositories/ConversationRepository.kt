package com.tivasoft.biconui.data.source.repositories

import android.content.SharedPreferences
import androidx.paging.*
import com.tivasoft.biconui.data.mapper.ChatMapper
import com.tivasoft.biconui.data.model.local.database.ChatMessageCacheEntity
import com.tivasoft.biconui.data.model.local.ui.chat.SeenInfo
import com.tivasoft.biconui.data.model.network.responses.common.ChatMessage
import com.tivasoft.biconui.data.source.ConversationRemoteMediator
import com.tivasoft.biconui.data.source.local.ChatMessagesDao
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ConversationRepository constructor(
    private val networkApi: NetworkApi,
    private val chatMessagesDao: ChatMessagesDao,
    private val chatMapper: ChatMapper,
    private val sharedPreferences: SharedPreferences
) {
    suspend fun setSeen(seenInfo: SeenInfo): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val options = mapOf(
                "currentUser" to seenInfo.currentUser,
                "chatId" to seenInfo.chatId
            )
            val response = networkApi.seenChatMessages(options = options)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getConversations(
        firstUserId: String,
        secondUserId: String
    ): Pager<Int, ChatMessageCacheEntity> {
        return Pager(
            config = PagingConfig(
                pageSize = 100,
                maxSize = 300,
                enablePlaceholders = false,
            ),
            remoteMediator = ConversationRemoteMediator(
                api = networkApi,
                chatMapper = chatMapper,
                chatMessagesDao = chatMessagesDao,
                user1Id = firstUserId,
                user2Id = secondUserId,
                pref = sharedPreferences
            )
        ) {
            chatMessagesDao.get(firstUserId, secondUserId)
        }
    }

    suspend fun addNewMessage(message: Any) {
        val entity: ChatMessage = when (message) {
            is ChatMessage -> message
            else -> Gson().fromJson(message.toString(), ChatMessage::class.java)
        }
        val cacheEntity = chatMapper.mapFromEntity(entity)
        chatMessagesDao.insert(cacheEntity)
    }

    suspend fun removeMessage(sender: String, receiver: String) {
        chatMessagesDao.delete(sender, receiver)
    }
}