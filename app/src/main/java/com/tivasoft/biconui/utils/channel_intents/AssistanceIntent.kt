package com.tivasoft.biconui.utils.channel_intents

import com.tivasoft.biconui.data.model.network.requests.assistant.RejectDoctorByAssistanceRequest
import com.tivasoft.biconui.data.model.network.requests.assistant.BookDoctorRequest
import com.tivasoft.biconui.data.model.network.requests.assistant.BookDoctorsRequest

sealed class AssistanceIntent {
    object GetDoctors : AssistanceIntent()
    object GetDoctorDetails : AssistanceIntent()
    class ConfirmDoctor(val entity: BookDoctorRequest) : AssistanceIntent()
    class RejectDoctor(val id: RejectDoctorByAssistanceRequest) : AssistanceIntent()
    class SubmitRequest(val entity: BookDoctorsRequest) : AssistanceIntent()
}