package com.tivasoft.biconui.data.model.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChatMessages")
data class ChatMessageCacheEntity(
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "seen")
    val seen: Boolean,
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val _id: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "sender")
    val sender: String,
    @ColumnInfo(name = "receiver")
    val receiver: String,
)