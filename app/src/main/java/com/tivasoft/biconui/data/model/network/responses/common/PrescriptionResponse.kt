package com.tivasoft.biconui.data.model.network.responses.common

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName
import java.util.*


data class PrescriptionResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<PrescriptionData>
)

data class PrescriptionData(
    val type: Int,
    val data: PrescriptionDataItem
)

data class PrescriptionDataItem(
    val id: String,
    @Nullable
    val gameId: String? = null,
    @SerializedName("name")
    val title: String
)

enum class PrescriptionType(val value: Int) {
    TEST(1),
    GAME(2),
    PODCAST(3),
    BREATH(4)
}
