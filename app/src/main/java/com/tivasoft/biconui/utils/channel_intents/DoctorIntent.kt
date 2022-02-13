package com.tivasoft.biconui.utils.channel_intents


import com.tivasoft.biconui.data.model.network.requests.doctor.RejectAssistanceRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.BookAssistance

import com.tivasoft.biconui.data.model.network.requests.doctor.ScheduleRequest

/**
 * Generic class for telling viewModel what should be done and with what parameters.
 */
sealed class DoctorIntent {
    object GetAllDegrees : DoctorIntent()
    object GetAllSpecialties : DoctorIntent()
    object GetAllPatient : DoctorIntent()
    object GetAllAssistance : DoctorIntent()
    object GetAssistanceDetails : DoctorIntent()
    object GetAllClinics : DoctorIntent()
    class UpdateDoctor(val entity: com.tivasoft.biconui.data.model.network.requests.doctor.UpdateDoctor) :
        DoctorIntent()

    class AddClinic(val entity: com.tivasoft.biconui.data.model.network.requests.doctor.AddClinic) :
        DoctorIntent()

    class CreateSchedule(val schedule: ScheduleRequest) : DoctorIntent()
    class ConfirmAssistance(val entity: BookAssistance) : DoctorIntent()
    class RejectAssistance(val id: RejectAssistanceRequest) : DoctorIntent()
    class UpdateClinic(
        val clinicId: String,
        val entity: com.tivasoft.biconui.data.model.network.requests.doctor.AddClinic
    ) : DoctorIntent()
}