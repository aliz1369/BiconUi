package com.tivasoft.biconui.data.model.network.requests.doctor

data class CreatePackageRequest(
    val title : String,
    val description : String,
    val price : Int,
    val period : String,
    val items : ArrayList<ItemPackageData>
)

data class ItemPackageData(
    var name : String,
    var unit : Int,
    var free : Boolean,
    var value : Int,
)
