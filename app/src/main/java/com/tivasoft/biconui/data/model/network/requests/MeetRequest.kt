package com.tivasoft.biconui.data.model.network.requests

data class MeetRequest(
    val thisUserId: String,
    val otherUserId: String,
    val type: String
)