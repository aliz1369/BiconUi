package com.tivasoft.biconui.data.source.local

import androidx.paging.PagingSource
import androidx.room.*
import com.tivasoft.biconui.data.model.local.database.ChatMessageCacheEntity

@Dao
interface ChatMessagesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chatMessage: ChatMessageCacheEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(chatMessages: List<ChatMessageCacheEntity>)

    @Query(
        "SELECT * FROM ChatMessages " +
                "WHERE (sender = :currentUserId AND receiver = :otherUserId) " +
                "OR (sender = :otherUserId AND receiver = :currentUserId) " +
                "ORDER BY time DESC"
    )
    fun get(currentUserId: String, otherUserId: String): PagingSource<Int, ChatMessageCacheEntity>

    @Query("DELETE FROM ChatMessages")
    fun deleteAll()

    @Query(
        "DELETE FROM ChatMessages " +
                "WHERE _id = (SELECT _id FROM ChatMessages WHERE " +
                "(sender = :sender AND receiver = :receiver) AND type = :type " +
                "ORDER BY time DESC LIMIT 1)"
    )
    suspend fun delete(sender: String, receiver: String, type: String = "uploading")
}