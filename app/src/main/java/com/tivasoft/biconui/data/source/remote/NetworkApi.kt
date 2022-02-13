package com.tivasoft.biconui.data.source.remote

import com.tivasoft.biconui.data.model.network.requests.doctor.AllClinicRequest
import com.tivasoft.biconui.data.model.network.responses.common.SocketFilesResponse
import com.tivasoft.biconui.data.model.network.requests.auth.*
import com.tivasoft.biconui.data.model.network.requests.doctor.CreatePackageRequest
import com.tivasoft.biconui.data.model.network.requests.MeetRequest
import com.tivasoft.biconui.data.model.network.requests.RegisterPushyTokenRequest
import com.tivasoft.biconui.data.model.network.responses.common.ConversationResponse
import com.tivasoft.biconui.data.model.network.requests.test.TestResultRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.AddClinic
import com.tivasoft.biconui.data.model.network.requests.patient.BookRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.BookAssistance
import com.tivasoft.biconui.data.model.network.requests.assistant.BookDoctorRequest
import com.tivasoft.biconui.data.model.network.requests.assistant.BookDoctorsRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.PatientIdModel
import com.tivasoft.biconui.data.model.network.requests.doctor.ScheduleRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.UpdateDoctor
import com.tivasoft.biconui.data.model.network.requests.patient.UpdatePatientRequest
import com.tivasoft.biconui.data.model.network.requests.auth.ResetPasswordRequest
import com.tivasoft.biconui.data.model.network.responses.assistant.DoctorByAssistanceResponse
import com.tivasoft.biconui.data.model.network.responses.assistant.GetDoctorDetailsResponse
import com.tivasoft.biconui.data.model.network.responses.auth.*
import com.tivasoft.biconui.data.model.network.responses.common.*
import com.tivasoft.biconui.data.model.network.responses.doctor.*
import com.tivasoft.biconui.data.model.network.responses.patient.BookResponse
import com.tivasoft.biconui.data.model.network.responses.patient.DoctorByPatientResponse
import com.tivasoft.biconui.data.model.network.responses.patient.NearClinicResponse
import com.tivasoft.biconui.data.model.network.responses.tests.BackPack
import com.tivasoft.biconui.data.model.network.responses.tests.SingleTest
import com.tivasoft.biconui.data.model.network.responses.tests.TestResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * an interface that indicates route, method,
 * parameters, and response model for each API.
 */
interface NetworkApi {
    @POST("auth/register")
    suspend fun register(@Body register: Register): RegisterResponse

    @POST("auth/login")
    suspend fun login(@Body login: Login): PhoneLoginResponse

    @POST("auth/verifyActiveCode")
    suspend fun verifyCode(@Body verify: Verify): VerifyCodeLoginResponse

    @PUT("auth/updateDetails")
    suspend fun updateUser(@Body user: User): UserResponse

    @POST("patients")
    suspend fun createPatient(@Body patientInfo: PatientInfo): PatientResponse

    @POST("doctors")
    suspend fun createDoctor(@Body doctorInfo: DoctorInfo): DoctorResponse

    @POST("auth/forgotPassword/sendActiveCode")
    suspend fun forgetPassword(@Body forgetPassword: ForgetPassword): ForgetPasswordResponse

    @GET("auth/me")
    suspend fun getMe(): GetMeResponse

    @GET("doctors/needAssistant")
    suspend fun getSearchDoctorForAssistance(
        @QueryMap options: Map<String, String>
    ): SearchDoctorResponse

    @Multipart
    @POST("auth/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): UploadResponse

    @GET("doctors")
    suspend fun getAllDoctors(@Query("search") keyWord: String): SearchDoctorResponse

    @GET("specialties/degrees")
    suspend fun getAllDegrees(): DegreeResponse

    @GET("specialties")
    suspend fun getAllSpecialties(): SpecialtiesResponse

    @PUT("doctors")
    suspend fun updateDoctor(@Body updateDoctor: UpdateDoctor): UpdateResponse

    @POST("doctors/clinics")
    suspend fun addClinic(@Body addClinic: AddClinic)

    @GET("doctors/specialties/{id}")
    suspend fun getDoctorsBySpecialties(@Path("id") id: String): SearchDoctorResponse

    @POST("doctors/booking")
    suspend fun book(@Body doctorId: BookRequest): BookResponse

    @GET("doctors/patients")
    suspend fun getAllPatientOfDoctor(): ConversationResponse

    @GET("prescriptions")
    suspend fun getAllPrescription(): PrescriptionResponse

    @POST("doctors/patients/confirm")
    suspend fun confirmPatient(@Body patientIdModelId: PatientIdModel): ConversationResponse

    @POST("doctors/patients/reject")
    suspend fun rejectPatient(@Body patientIdModelId: PatientIdModel): ConversationResponse

    @GET("doctors")
    suspend fun getFilteredDoctors(
        @Query("genderType") genderType: String,
        @Query("specialty") specialty: String,
        @Query("consulting") consulting: String,
        @Query("class") clas: String,
        @Query("search") search: String
    ): SearchDoctorResponse

    @GET("chats")
    suspend fun getChatHistory(@QueryMap options: Map<String, String>): ChatHistory

    @GET("p2p-chat")
    suspend fun getP2PChatHistory(@QueryMap options: Map<String, String>): ChatHistory

    @GET("patients")
    suspend fun getAllPatient(): GetAllPatientResponse

    @GET("prescriptions/backpack")
    suspend fun getBackPack(): BackPack

    @GET("prescriptions/backpack")
    suspend fun getBackPack(
        @Query("patient") patientId: String
    ): BackPack

    @GET("tests/{id}")
    suspend fun getSingleTestById(@Path("id") testId: String): SingleTest

    @POST("tests/answers")
    suspend fun sendTestResult(@Body testResultRequest: TestResultRequest): TestResult

    @POST("schedules")
    suspend fun createSchedule(@Body schedule: ScheduleRequest)

    @GET("schedules")
    suspend fun getAllSchedules(): AllScheduleResponse

    @POST("doctors/clinics/searchByMap")
    suspend fun getNearClinics(@Body currentLocation: AllClinicRequest): NearClinicResponse

    @GET("doctors/assistants")
    suspend fun getAllAssistance(): AssistanceResponse

    @POST("doctors/assistants/confirm")
    suspend fun confirmAssistance(@Body entity: BookAssistance)

    @GET("patients/doctors")
    suspend fun getDoctorsByPatient(): DoctorByPatientResponse

    @GET("schedules")
    suspend fun getSchedulesByDate(@Query("day") date: Long): AllScheduleResponse

    @GET("schedules/days")
    suspend fun getScheduledDays(@Query("date") date: Long): ScheduledDays

    @POST("register-notif-token")
    suspend fun registerPushyToken(
        @Body tokenRequest: RegisterPushyTokenRequest
    ): RegisterPushyTokenResponse

    @POST("call")
    suspend fun call(@Body meetRequest: MeetRequest): MeetResponse

    @POST("endcall")
    suspend fun endCall(@Body meetRequest: MeetRequest)

    @DELETE("doctors/assistants/reject/{id}")
    suspend fun rejectAssistance(@Path("id") id: String)

    @GET("assistants/doctors")
    suspend fun getAllDoctors(): DoctorByAssistanceResponse

    @POST("assistants/doctors")
    suspend fun sendRequestToDoctors(@Body entity: BookDoctorsRequest)

    @POST("assistants/doctors/confirm")
    suspend fun confirmDoctor(@Body entity: BookDoctorRequest)

    @DELETE("assistants/doctors/reject/{id}")
    suspend fun rejectDoctor(@Path("id") id: String)

    @GET("patients/packages")
    suspend fun getPatientPackageList(): PackageListResponse

    @GET("packages")
    suspend fun getAdminPackageList(): PackageListResponse

    @GET("packages/doctors/{doctorId}")
    suspend fun getDoctorPackagesById(
        @Path("doctorId") doctorId: String
    ): PackageListResponse

    @GET("doctors/packages")
    suspend fun getDoctorPackageList(): PackageListResponse

    @GET("doctors/packages/periods")
    suspend fun getPeriods(): PeriodResponse

    @GET("assistants/doctors/details")
    suspend fun getDoctorDetails(): GetDoctorDetailsResponse

    @POST("auth/resetPassword")
    suspend fun resetPassword(@Body newPassword: ResetPasswordRequest)

    @GET("doctors/packages/items")
    suspend fun getItems(): GetItemResponse

    @POST("doctors/packages")
    suspend fun createPackage(@Body entity: CreatePackageRequest): CreatePackageResponse

    @GET("doctors/assistants/details")
    suspend fun getAssistanceDetails(): GetAssistanceDetailsResponse

    @POST("files")
    suspend fun uploadChatFile(@Body requestBody: RequestBody): SocketFilesResponse

    @GET("seen")
    suspend fun seenChatMessages(@QueryMap options: Map<String, String>)

    @DELETE("doctors/packages/{packageId}")
    suspend fun changePackageActivation(@Path("packageId") packageId: String)

    @GET("doctors/clinics")
    suspend fun getAllClinics(): AddClinicResponse

    @PUT("doctors/clinics/{clinicId}")
    suspend fun updateClinic(
        @Path("clinicId") clinicId: String,
        @Body addClinic: AddClinic
    )

    @PUT("patients")
    suspend fun updatePatient(@Body entity: UpdatePatientRequest)

    @GET("schedules/months")
    suspend fun getScheduledMonths(@Query("date") date: Long): ScheduledMonths
}