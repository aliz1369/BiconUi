package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.model.local.ui.SearchDoctorFilterModel
import com.tivasoft.biconui.data.model.network.requests.auth.*
import com.tivasoft.biconui.data.model.network.requests.auth.ResetPasswordRequest
import com.tivasoft.biconui.data.model.network.responses.auth.ForgetPasswordResponse
import com.tivasoft.biconui.data.model.network.responses.common.GetMeResponse
import com.tivasoft.biconui.data.model.network.responses.auth.*
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Contains methods that will call on their respective APIs.
 */
class AuthRepository constructor(
    private val networkApi: NetworkApi
) {
    suspend fun register(entity: Register): Flow<DataState<RegisterResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.register(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    /**
     * Send a Login entity to login API and emit the result for each state.
     *
     * @param entity the Login entity that we send to API
     *
     * @return API response in form of a coroutine flow
     */
    suspend fun login(entity: Login): Flow<DataState<PhoneLoginResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.login(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun verify(entity: Verify): Flow<DataState<VerifyCodeLoginResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.verifyCode(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun updateUser(entity: User): Flow<DataState<UserResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.updateUser(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun createPatient(entity: PatientInfo): Flow<DataState<PatientResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.createPatient(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun createDoctor(entity: DoctorInfo): Flow<DataState<DoctorResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.createDoctor(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun forgetPassword(entity: ForgetPassword): Flow<DataState<ForgetPasswordResponse>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.forgetPassword(entity)
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }

    suspend fun getMe(): Flow<DataState<GetMeResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getMe()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun searchDoctorForAssistance(
        entity: SearchDoctorFilterModel
    ): Flow<DataState<SearchDoctorResponse>> = flow {
        emit(DataState.Loading)
        try {
            val options = mapOf(
                "genderType" to entity.genderType,
                "grade" to entity.grade,
                "sort" to entity.sort
            )
            val response = networkApi.getSearchDoctorForAssistance(options)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun resetPassword(entity: ResetPasswordRequest): Flow<DataState<Unit>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.resetPassword(entity)
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }
}