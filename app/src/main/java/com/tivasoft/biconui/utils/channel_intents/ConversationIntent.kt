package com.tivasoft.biconui.utils.channel_intents

import com.tivasoft.biconui.data.model.network.requests.doctor.PatientIdModel

sealed class ConversationIntent {
    object GetDoctorsByPatient : ConversationIntent()
    object GetAllPatientOfDoctor : ConversationIntent()
    class ConfirmNewPatient(val entity: PatientIdModel) : ConversationIntent()
    class RejectPatient(val entity: PatientIdModel) : ConversationIntent()
}