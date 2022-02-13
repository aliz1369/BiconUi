package com.tivasoft.biconui.data.model.network.requests.assistant

data class BookDoctorsRequest(
    val doctors: ArrayList<String>
)

data class BookDoctorRequest(
    val doctor: String
)
