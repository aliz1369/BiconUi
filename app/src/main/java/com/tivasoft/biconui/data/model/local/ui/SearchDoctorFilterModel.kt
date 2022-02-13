package com.tivasoft.biconui.data.model.local.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchDoctorFilterModel(
    val genderType: String = "",
    val grade: String = "",
    var sort: String = ""
) : Parcelable