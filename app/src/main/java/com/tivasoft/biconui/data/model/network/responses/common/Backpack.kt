package com.tivasoft.biconui.data.model.network.responses.common

import androidx.annotation.DrawableRes

data class Backpack(
    val id: String,
    val title: String,
    val availability: String,
    @DrawableRes val icon: Int,
    val isFree: Boolean
)