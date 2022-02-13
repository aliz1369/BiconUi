package com.tivasoft.biconui.data.model.local.ui

import androidx.annotation.DrawableRes

data class Prescription(
    val id: Int,
    val title: String,
    @DrawableRes val background: Int,
    val prescriptionType: PrescriptionType
)

enum class PrescriptionType {
    SingleColumn, DoubleColumn
}