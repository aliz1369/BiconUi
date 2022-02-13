package com.tivasoft.biconui.utils.channel_intents

import com.tivasoft.biconui.data.model.local.ui.SearchDoctorFilterModel
import com.tivasoft.biconui.data.model.network.requests.auth.*
import com.tivasoft.biconui.data.model.network.requests.auth.ResetPasswordRequest

/**
 * Generic class for telling viewModel what should be done and with what parameters.
 */
sealed class AuthIntent {
    class RegisterIntent(val registerEntity: Register) : AuthIntent()
    class VerifyIntent(val verifyEntity: Verify) : AuthIntent()
    class UpdateUserIntent(val userEntity: User) : AuthIntent()
    class LoginIntent(val loginEntity: Login) : AuthIntent()
    class CreatePatient(val patientEntity: PatientInfo) : AuthIntent()
    class CreateDoctor(val doctorEntity: DoctorInfo) : AuthIntent()
    class ForgetPasswordIntent(val ForgetPasswordEntity: ForgetPassword) : AuthIntent()
    class ResetPasswordIntent(val resetEntity: ResetPasswordRequest) : AuthIntent()
    class SearchDoctorForAssistanceIntent(val entity: SearchDoctorFilterModel) : AuthIntent()
    object GetMeIntent : AuthIntent()
}