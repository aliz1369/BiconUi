package com.tivasoft.biconui.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tivasoft.biconui.data.model.local.database.PrescriptionsCacheEntity
import com.tivasoft.biconui.data.model.local.database.ChatMessageCacheEntity

@Database(
    entities = [ChatMessageCacheEntity::class, PrescriptionsCacheEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessagesDao
    abstract fun prescriptionsDao(): PrescriptionsDao
}