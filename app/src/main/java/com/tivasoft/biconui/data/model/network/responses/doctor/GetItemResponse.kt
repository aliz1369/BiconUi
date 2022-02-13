package com.tivasoft.biconui.data.model.network.responses.doctor

data class GetItemResponse(
    val success : Boolean,
    val statusCode : Int,
    val data : ArrayList<ItemData>
)

data class ItemData(
    val id : String,
    val title : String,
    val unit : Int
)
