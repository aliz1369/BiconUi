package com.tivasoft.biconui.utils.channel_intents

import com.tivasoft.biconui.data.model.network.requests.patient.BookRequest
import com.tivasoft.biconui.data.model.network.requests.patient.FilterModel

/**
 * Generic class for telling viewModel what should be done and with what parameters.
 */
sealed class SearchIntent {

    class GetAllDoctor(val keyWord:String): SearchIntent()
    class GetAllDoctorBySpecialties(val id:String): SearchIntent()
    object GetAllSpecialties : SearchIntent()
    object GetAllPrescription : SearchIntent()
    class Book(val bookEntity: BookRequest): SearchIntent()
    class GetFilteredDoctor(val filterModel: FilterModel): SearchIntent()
}