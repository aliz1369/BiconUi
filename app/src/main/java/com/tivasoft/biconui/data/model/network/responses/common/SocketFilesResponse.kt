package com.tivasoft.biconui.data.model.network.responses.common

data class SocketFilesResponse(
    val success: Boolean,
    val msg: String,
    val item: SocketFilesItem
)

data class SocketFilesItem(
    val url: String,
    val sender: String,
    val receiver: String,
    val type: String
)