package com.tivasoft.biconui.data.model.network.requests.test

import com.google.gson.annotations.SerializedName

data class TestResultRequest(
    @SerializedName("test")
    val testId: String,
    val scores: ArrayList<Int>
)