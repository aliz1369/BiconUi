package com.tivasoft.biconui.data.model.network.responses.auth

/**
 * data model for SignIn API Response.
 */
data class SignInResponse(
    val success: Boolean,
    val accountType: Int,
    val token: String
)