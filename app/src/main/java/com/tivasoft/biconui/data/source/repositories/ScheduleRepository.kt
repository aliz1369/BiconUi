package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.mapper.ScheduleMapper
import com.tivasoft.biconui.data.model.local.ui.ScheduleItem
import com.tivasoft.biconui.data.model.local.ui.ScheduleType
import com.tivasoft.biconui.data.model.network.responses.common.ScheduledDays
import com.tivasoft.biconui.data.model.network.responses.common.ScheduledMonths
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Contains methods that will call on their respective APIs.
 */
class ScheduleRepository constructor(
    private val networkApi: NetworkApi,
    private val mapper: ScheduleMapper
) {
    suspend fun getSchedulesByDate(date: Long): Flow<DataState<ArrayList<ScheduleItem>>> = flow {
        emit(DataState.Loading)
        try {
            val networkResponse = networkApi.getSchedulesByDate(date)
            val todayList = networkResponse.data.today
            val nextItems = networkResponse.data.next

            val todaySchedule = if (todayList.isNullOrEmpty()) {
                listOf(
                    ScheduleItem(
                        id = "",
                        scheduleType = ScheduleType.EMPTY
                    )
                )
            } else mapper.mapFromEntityList(
                todayList.subList(
                    0, minOf(todayList.size, 3)
                )
            )
            val nextSchedule = if (nextItems.isNullOrEmpty()) {
                listOf(
                    ScheduleItem(
                        id = "",
                        scheduleType = ScheduleType.EMPTY
                    )
                )
            } else mapper.mapFromEntityList(
                nextItems.subList(
                    0, minOf(nextItems.size, 3)
                )
            )
            val response = ArrayList(todaySchedule)
            response.add(
                ScheduleItem(
                    id = "",
                    scheduleType = ScheduleType.HEADER
                )
            )
            response.addAll(nextSchedule)
            emit(DataState.Success(ArrayList(response)))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getScheduledDays(date: Long): Flow<DataState<ScheduledDays>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getScheduledDays(date)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getScheduledMonths(date: Long): Flow<DataState<ScheduledMonths>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getScheduledMonths(date)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }
}