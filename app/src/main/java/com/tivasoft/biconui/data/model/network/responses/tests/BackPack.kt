package com.tivasoft.biconui.data.model.network.responses.tests

data class BackPack(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<BackPackItem>
)

data class BackPackItem(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
)