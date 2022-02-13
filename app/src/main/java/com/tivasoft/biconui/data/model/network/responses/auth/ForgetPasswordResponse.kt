package com.tivasoft.biconui.data.model.network.responses.auth

data class ForgetPasswordResponse(
        val success : Boolean,
        val statusCode : Int,
        val data : Int
)