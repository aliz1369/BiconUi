package com.tivasoft.biconui.data.mapper

import com.tivasoft.biconui.data.model.local.database.PrescriptionsCacheEntity
import com.tivasoft.biconui.data.model.network.responses.common.PrescriptionData
import com.tivasoft.biconui.utils.EntityMapper
import javax.inject.Inject

class PrescriptionsCacheMapper @Inject constructor() :
    EntityMapper<PrescriptionData, PrescriptionsCacheEntity> {
    override fun mapFromEntity(entity: PrescriptionData) = PrescriptionsCacheEntity(
        id = entity.data.gameId ?: entity.data.id,
        title = entity.data.title
    )

    override fun mapFromEntityList(entities: List<PrescriptionData>):
            List<PrescriptionsCacheEntity> = entities.map { mapFromEntity(it) }
}