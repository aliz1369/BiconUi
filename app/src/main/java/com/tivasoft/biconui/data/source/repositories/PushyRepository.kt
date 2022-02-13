package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.model.network.requests.RegisterPushyTokenRequest
import com.tivasoft.biconui.data.model.network.responses.common.RegisterPushyTokenResponse
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Contains methods that will call on their respective APIs.
 */
class PushyRepository constructor(
    private val networkApi: NetworkApi
) {
    suspend fun registerPushyToken(tokenRequest: RegisterPushyTokenRequest):
            Flow<DataState<RegisterPushyTokenResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.registerPushyToken(tokenRequest = tokenRequest)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }
}