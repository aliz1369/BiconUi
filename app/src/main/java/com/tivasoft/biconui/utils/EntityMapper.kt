package com.tivasoft.biconui.utils

/**
 * Generic class providing mapping interface in other classes.
 *
 * @param Entity we want map to/from
 * @param DomainModel we want map to/from
 *
 */
interface EntityMapper<Entity, DomainModel> {
    fun mapFromEntity(entity: Entity): DomainModel
    fun mapFromEntityList(entities: List<Entity>): List<DomainModel>
}