package com.tivasoft.biconui.data.model.network.responses.auth

data class PhoneLoginResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: String
)
