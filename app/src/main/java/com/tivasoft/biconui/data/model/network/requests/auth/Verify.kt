package com.tivasoft.biconui.data.model.network.requests.auth
/**
 * data model for Verify API Request.
 */
data class Verify(
    private val phoneNumber: String,
    private val code: Int,
)