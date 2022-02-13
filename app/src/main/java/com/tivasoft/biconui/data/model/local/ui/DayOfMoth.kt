package com.tivasoft.biconui.data.model.local.ui

data class DayOfMoth(
    val day: String,
    val isToday: Boolean,
    var hasEvent: Boolean = false,
    var isSelected: Boolean = false,
)