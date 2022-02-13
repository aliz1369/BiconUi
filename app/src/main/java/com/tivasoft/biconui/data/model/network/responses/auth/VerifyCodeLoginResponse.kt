package com.tivasoft.biconui.data.model.network.responses.auth

data class VerifyCodeLoginResponse(
    val success: Boolean,
    val token: String,
    val accountType: Int
)
