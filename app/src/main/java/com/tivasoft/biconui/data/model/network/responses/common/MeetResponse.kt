package com.tivasoft.biconui.data.model.network.responses.common

import com.google.gson.annotations.SerializedName

data class MeetResponse(
    val success: Boolean,
    val msg: String,
    val item: MeetResponseItem
)

data class MeetResponseItem(
    val _id: String,
    val user1: String,
    val user2: String,
    @SerializedName("romeName")
    val roomName: String,
    val times: ArrayList<MeetTimes>,
    val createdAt: String,
    val updatedAt: String,
    val __v: String
)

data class MeetTimes(
    val start: String,
    val end: String,
    val status: String,
    val _id: String
)