package com.tivasoft.biconui.data.model.network.responses.tests

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingleTest(
    val success: Boolean,
    val statusCode: Int,
    val data: TestData
) : Parcelable

@Parcelize
data class TestData(
    val id: String,
    val name: String,
    val description: String,
    val type: Int,
    val items: ArrayList<Test>,
    val createdAt: String,
    val updatedAt: String
) : Parcelable

@Parcelize
data class Test(
    val id: String,
    val question: String,
    val image: String,
    val options: ArrayList<TestOptions>
) : Parcelable

@Parcelize
data class TestOptions(
    @SerializedName(value = "name", alternate = ["url"])
    val itemValue: String,
    val score: Int
) : Parcelable