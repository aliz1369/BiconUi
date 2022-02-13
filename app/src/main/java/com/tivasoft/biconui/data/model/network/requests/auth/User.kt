package com.tivasoft.biconui.data.model.network.requests.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import javax.annotation.Nullable

@Parcelize
data class User(
    @Nullable var fullName: String? = "",
    @Nullable var password: String? = "",
) : Parcelable