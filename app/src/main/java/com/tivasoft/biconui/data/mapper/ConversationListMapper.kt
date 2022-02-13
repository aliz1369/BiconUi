package com.tivasoft.biconui.data.mapper

import com.tivasoft.biconui.data.model.network.responses.common.ConversationData
import com.tivasoft.biconui.data.model.network.responses.common.ConversationResponse
import com.tivasoft.biconui.data.model.network.responses.patient.DoctorByPatientResponse
import com.tivasoft.biconui.data.model.network.responses.patient.DoctorData
import com.tivasoft.biconui.utils.EntityMapper
import javax.inject.Inject

class ConversationListMapper @Inject constructor() :
    EntityMapper<DoctorByPatientResponse, ConversationResponse> {
    override fun mapFromEntity(entity: DoctorByPatientResponse) = ConversationResponse(
        success = entity.success,
        statusCode = entity.statusCode,
        data = ArrayList(ConversationDataMapper().mapFromEntityList(entity.data))
    )

    override fun mapFromEntityList(entities: List<DoctorByPatientResponse>) =
        listOf<ConversationResponse>()

    private inner class ConversationDataMapper : EntityMapper<DoctorData, ConversationData> {
        override fun mapFromEntity(entity: DoctorData) = ConversationData(
            id = entity.id,
            patientName = entity.fullName,
            patientIconUrl = entity.photo,
            patientStatus = 4,
            symptom = "",
            newMessageType = 1
        )

        override fun mapFromEntityList(entities: List<DoctorData>): List<ConversationData> =
            entities.map { mapFromEntity(it) }
    }
}