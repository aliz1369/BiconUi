package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.model.network.requests.doctor.RejectAssistanceRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.AddClinic
import com.tivasoft.biconui.data.model.network.requests.doctor.BookAssistance
import com.tivasoft.biconui.data.model.network.requests.doctor.PatientIdModel
import com.tivasoft.biconui.data.model.network.requests.doctor.ScheduleRequest
import com.tivasoft.biconui.data.model.network.requests.doctor.UpdateDoctor
import com.tivasoft.biconui.data.model.network.responses.common.ConversationResponse
import com.tivasoft.biconui.data.model.network.responses.common.UpdateResponse
import com.tivasoft.biconui.data.model.network.responses.doctor.*
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DoctorRepository constructor(
    private val networkApi: NetworkApi
) {
    suspend fun getAllDegrees(): Flow<DataState<DegreeResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAllDegrees()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getAllSpecialties(): Flow<DataState<SpecialtiesResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAllSpecialties()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun updateDoctor(entity: UpdateDoctor): Flow<DataState<UpdateResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.updateDoctor(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun addClinic(entity: AddClinic): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.addClinic(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getAllPatientOfDoctor(): Flow<DataState<ConversationResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAllPatientOfDoctor()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun confirmPatient(entity: PatientIdModel): Flow<DataState<ConversationResponse>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.confirmPatient(entity)
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }

    suspend fun getAllPatient(): Flow<DataState<GetAllPatientResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAllPatient()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun createSchedule(entity: ScheduleRequest): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.createSchedule(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getAllAssistance(): Flow<DataState<AssistanceResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAllAssistance()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun confirmAssistance(entity: BookAssistance): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.confirmAssistance(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun rejectAssistance(entity: RejectAssistanceRequest): Flow<DataState<Unit>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.rejectAssistance(entity.id)
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }

    suspend fun getAssistanceDetails(): Flow<DataState<GetAssistanceDetailsResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAssistanceDetails()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getAllClinics(): Flow<DataState<AddClinicResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAllClinics()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun updateClinic(
        clinicId: String,
        entity: AddClinic
    ): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.updateClinic(clinicId, entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun rejectPatient(entity: PatientIdModel): Flow<DataState<ConversationResponse>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.rejectPatient(entity)
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }
}