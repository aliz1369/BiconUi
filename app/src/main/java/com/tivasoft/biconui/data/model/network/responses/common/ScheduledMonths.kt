package com.tivasoft.biconui.data.model.network.responses.common

import java.util.*

data class ScheduledMonths(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<ArrayList<Int>>
)