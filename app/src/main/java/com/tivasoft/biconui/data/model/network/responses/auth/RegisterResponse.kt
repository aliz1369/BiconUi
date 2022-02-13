package com.tivasoft.biconui.data.model.network.responses.auth
/**
 * data model for Register API Response.
 */
data class RegisterResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: Int
)