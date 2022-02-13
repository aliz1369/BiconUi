package com.tivasoft.biconui.data.model.network.responses.auth

data class UserResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: UserData
)

data class UserData(
    val id: String,
    val phoneNumber: String,
    val fullName: String,
    val accountType: Int,
    val createdAt: String,
    val updatedAt: String
)