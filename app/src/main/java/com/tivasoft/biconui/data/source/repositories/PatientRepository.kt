package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.mapper.ConversationListMapper
import com.tivasoft.biconui.data.mapper.PrescriptionsCacheMapper
import com.tivasoft.biconui.data.mapper.ScheduleMapper
import com.tivasoft.biconui.data.model.network.requests.doctor.AllClinicRequest
import com.tivasoft.biconui.data.model.local.ui.ScheduleItem
import com.tivasoft.biconui.data.model.local.ui.ScheduleType
import com.tivasoft.biconui.data.model.network.requests.patient.BookRequest
import com.tivasoft.biconui.data.model.network.requests.patient.UpdatePatientRequest
import com.tivasoft.biconui.data.model.network.responses.patient.BookResponse
import com.tivasoft.biconui.data.model.network.responses.common.ConversationResponse
import com.tivasoft.biconui.data.model.network.responses.patient.NearClinicResponse
import com.tivasoft.biconui.data.model.network.responses.common.PrescriptionResponse
import com.tivasoft.biconui.data.source.local.PrescriptionsDao
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PatientRepository constructor(
    private val networkApi: NetworkApi,
    private val mapper: ScheduleMapper,
    private val conversationListMapper: ConversationListMapper,
    private val prescriptionsDao: PrescriptionsDao,
    private val prescriptionsCacheMapper: PrescriptionsCacheMapper
) {
    suspend fun book(entity: BookRequest): Flow<DataState<BookResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.book(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getAllPrescription(): Flow<DataState<PrescriptionResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAllPrescription()
            val cachedResponse = prescriptionsCacheMapper.mapFromEntityList(response.data)
            prescriptionsDao.insertAll(cachedResponse)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getAllSchedules(): Flow<DataState<List<ScheduleItem>>> = flow {
        emit(DataState.Loading)
        try {
            val networkResponse = networkApi.getAllSchedules()
            val schedules = networkResponse.data.today
            schedules.addAll(networkResponse.data.next)
            val response = if (schedules.isNullOrEmpty()) {
                listOf(
                    ScheduleItem(
                        id = "",
                        scheduleType = ScheduleType.EMPTY
                    )
                )
            } else mapper.mapFromEntityList(schedules)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getNearClinic(entity: AllClinicRequest): Flow<DataState<NearClinicResponse>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.getNearClinics(entity)
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }

    suspend fun getDoctorsByPatient(): Flow<DataState<ConversationResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getDoctorsByPatient()
            val mappedResponse = conversationListMapper.mapFromEntity(response)
            emit(DataState.Success(mappedResponse))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun updatePatient(entity: UpdatePatientRequest): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.updatePatient(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }
}