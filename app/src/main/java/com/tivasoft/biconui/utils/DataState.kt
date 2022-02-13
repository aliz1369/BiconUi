package com.tivasoft.biconui.utils

import com.tivasoft.biconui.data.model.network.responses.common.ErrorResponseModel
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

/**
 * Generic class for storing API result in each state.
 */
sealed class DataState<out R> {
    object Idle : DataState<Nothing>()
    object Loading : DataState<Nothing>()
    class Success<out T>(val data: T) : DataState<T>()
    class Failed(val errorResponse: ErrorResponse) : DataState<Nothing>()
}

class ErrorResponse(val exception: Exception) {
    fun getErrorMessage(): String = when (exception) {
        is IOException -> "No Network"
        is HttpException -> convertErrorBody(exception)
        else -> "Unexpected Error"
    }

    private fun convertErrorBody(exception: HttpException) =
        exception.response()?.errorBody()?.string()?.let {
            Gson().fromJson(it, ErrorResponseModel::class.java)
        }?.errorMessage ?: "Unexpected Error"
}