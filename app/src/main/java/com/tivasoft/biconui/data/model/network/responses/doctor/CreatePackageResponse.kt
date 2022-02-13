package com.tivasoft.biconui.data.model.network.responses.doctor

data class CreatePackageResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: CreatePackageData
)
data class CreatePackageData(
    val id : String,
    val title : String,
    val description : String,
    val price : Int,
    val period : Int,
    val active : Boolean,
    val items : ArrayList<ItemsData>
)

data class ItemsData(
    val id : String,
    val name : String,
    val title : String,
    val value : Int,
    val free : Boolean,
    val unit : Int,
)

