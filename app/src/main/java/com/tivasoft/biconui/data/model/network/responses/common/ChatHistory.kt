package com.tivasoft.biconui.data.model.network.responses.common

data class ChatHistory(
    val success: Boolean,
    val msg: String,
    val messages: ArrayList<ChatMessage>,
)

data class ChatItem(
    val _id: String,
    val user1: String,
    val user2: String,
    val messages: ArrayList<ChatMessage>,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)

data class ChatMessage(
    val time: String,
    val seen: Boolean,
    val _id: String,
    val content: String,
    val type: String,
    val sender: String,
    val receiver: String,
)