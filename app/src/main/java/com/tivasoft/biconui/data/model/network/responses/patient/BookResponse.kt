package com.tivasoft.biconui.data.model.network.responses.patient

data class BookResponse(
    val success: Boolean,
    val statusCode : Int,
    val data : BookData
)

data class BookData(
     val id: String,
     val isConfirmedByDoctor : Boolean,
     val doctor : String,
     val patient: String
)
