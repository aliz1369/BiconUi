package com.tivasoft.biconui.data.model.local.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemEntity(
    val name: String,
    val value: Int,
    val free: Boolean,
    val unit: Int
): Parcelable
