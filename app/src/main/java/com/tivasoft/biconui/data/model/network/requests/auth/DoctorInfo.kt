package com.tivasoft.biconui.data.model.network.requests.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import javax.annotation.Nullable

/**
 * data model for DoctorInfo API Request.
 */
@Parcelize
data class DoctorInfo(
    @Nullable var educationDegree: String? = null,
    @Nullable var educationField: String? = null,
    var isAssistant: Boolean,
    @Nullable var attachment: ArrayList<String> = arrayListOf(),
    @Nullable var grade: Int? = null,
    @Nullable var commissionRate: Int? = null,
    @Nullable var assistant: Assistant? = null
) : Parcelable

@Parcelize
data class Assistant(
    var phoneNumber: String,
    var description: String,
    @Nullable var attachment: ArrayList<String> = arrayListOf(),
    @Nullable var doctorsId: ArrayList<String> = arrayListOf()
) : Parcelable