package com.tivasoft.biconui.utils.channel_intents

import com.tivasoft.biconui.data.model.network.requests.doctor.AllClinicRequest
import com.tivasoft.biconui.data.model.network.requests.RegisterPushyTokenRequest
import com.tivasoft.biconui.data.model.network.requests.patient.UpdatePatientRequest

sealed class PatientIntent {
    object GeAllSchedules : PatientIntent()
    class GeNearClinics(val entity: AllClinicRequest) : PatientIntent()
    class RegisterPushy(val entity: RegisterPushyTokenRequest) : PatientIntent()
    class UpdatePatient(val entity: UpdatePatientRequest) : PatientIntent()
}