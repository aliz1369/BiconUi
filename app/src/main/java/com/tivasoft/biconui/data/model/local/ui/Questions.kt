package com.tivasoft.biconui.data.model.local.ui

import androidx.annotation.DrawableRes

data class Questions(
    val id: Int,
    val questionText: String,
    val answers: ArrayList<Answers>
)

data class Answers(
    val id: Int,
    @DrawableRes val image: Int,
    val answerText: String
)