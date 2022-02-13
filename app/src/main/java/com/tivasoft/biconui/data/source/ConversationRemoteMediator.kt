package com.tivasoft.biconui.data.source

import android.content.SharedPreferences
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.tivasoft.biconui.data.mapper.ChatMapper
import com.tivasoft.biconui.data.model.local.database.ChatMessageCacheEntity
import com.tivasoft.biconui.data.source.local.ChatMessagesDao
import com.tivasoft.biconui.data.source.remote.NetworkApi

@OptIn(ExperimentalPagingApi::class)
class ConversationRemoteMediator(
    private val api: NetworkApi,
    private val chatMapper: ChatMapper,
    private val chatMessagesDao: ChatMessagesDao,
    private val user1Id: String,
    private val user2Id: String,
    private val pref: SharedPreferences
) : RemoteMediator<Int, ChatMessageCacheEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatMessageCacheEntity>
    ): MediatorResult {
        val loadKey = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.APPEND ->
                return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.PREPEND -> {
                state.pages.size + 1
            }
        }
        return try {
            val options = mapOf(
                "user1" to user1Id,
                "user2" to user2Id,
                "page" to "$loadKey"
            )
            val response = api.getP2PChatHistory(options = options)
            val messages = response.messages
            val mappedResponse = chatMapper.mapFromEntityList(messages)
            chatMessagesDao.insertAll(mappedResponse)
            pref.edit()
                .putLong("lastUpdated", System.currentTimeMillis())
                .putString("currentChatId", "response._id")
                .apply()
            MediatorResult.Success(endOfPaginationReached = messages.isEmpty())
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    override suspend fun initialize(): InitializeAction {
        val lastUpdated = pref.getLong("lastUpdated", 0)
        return if (pref.contains("lastUpdated") &&
            System.currentTimeMillis() - lastUpdated <= 2500
        ) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }
}