package com.tivasoft.biconui.data.model.network.responses.common

data class ConversationResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<ConversationData>
)


data class ConversationData(
    val id: String,
    val patientName: String,
    val patientIconUrl: String? = "",
    val patientStatus: Int,
    val symptom: String,
    val newMessageType: Int
)


enum class PatientStatus(val status: String) {
    NewPatient("New Patient"),
    CurrentPatient("Current Patient"),
    PreviousPatient("Previous Patient")
}

enum class NewMessageType {
    None,
    Request,
    TestResult,
    Message
}

