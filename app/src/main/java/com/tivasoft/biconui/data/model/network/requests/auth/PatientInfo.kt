package com.tivasoft.biconui.data.model.network.requests.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PatientInfo(
    var genderType: String,
    var age: String,
    @SerializedName("carrer")
    var career: String
) : Parcelable