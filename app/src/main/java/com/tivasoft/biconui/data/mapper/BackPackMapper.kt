package com.tivasoft.biconui.data.mapper

import com.tivasoft.biconui.data.model.network.responses.common.Backpack
import com.tivasoft.biconui.data.model.network.responses.tests.BackPackItem
import com.tivasoft.biconui.utils.EntityMapper
import javax.inject.Inject

class BackPackMapper @Inject constructor() : EntityMapper<BackPackItem, Backpack> {
    override fun mapFromEntity(entity: BackPackItem) = Backpack(
        id = entity.id,
        title = entity.name,
        availability = "Available",
        isFree = true,
        icon = 0,
    )

    override fun mapFromEntityList(entities: List<BackPackItem>): List<Backpack> {
        return entities.map { mapFromEntity(it) }
    }
}