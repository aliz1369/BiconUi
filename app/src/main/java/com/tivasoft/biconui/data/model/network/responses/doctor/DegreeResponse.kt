package com.tivasoft.biconui.data.model.network.responses.doctor

data class DegreeResponse(
    val success: Boolean,
    val statusCode : Int,
    val data : ArrayList<DegreeData>
)

data class DegreeData(
    val  degree: String
)
