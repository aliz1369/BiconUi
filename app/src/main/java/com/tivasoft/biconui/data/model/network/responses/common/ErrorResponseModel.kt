package com.tivasoft.biconui.data.model.network.responses.common

import com.google.gson.annotations.SerializedName

data class ErrorResponseModel(
    val success: Boolean,
    @SerializedName(value = "error", alternate = ["msg"])
    val errorMessage: String
)