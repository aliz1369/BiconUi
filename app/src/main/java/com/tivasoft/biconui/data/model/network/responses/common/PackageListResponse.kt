package com.tivasoft.biconui.data.model.network.responses.common

data class PackageListResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<PackageData>
)

data class PackageData(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val period: Int,
    var active: Boolean,
    val items: ArrayList<PackageItems>
)

data class PackageItems(
    val id: String,
    val name: String,
    val title: String,
    val value: Int,
    val free: Boolean,
    val unit: Int,
)
