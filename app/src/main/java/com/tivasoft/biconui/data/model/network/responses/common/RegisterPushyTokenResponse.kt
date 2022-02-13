package com.tivasoft.biconui.data.model.network.responses.common

data class RegisterPushyTokenResponse(
    val success: Boolean,
    val msg: String,
    val item: TokenItem
)

data class TokenItem(
    val _id: String,
    val user: String,
    val deviceToken: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)