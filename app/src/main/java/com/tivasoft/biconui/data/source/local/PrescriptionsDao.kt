package com.tivasoft.biconui.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tivasoft.biconui.data.model.local.database.PrescriptionsCacheEntity

@Dao
interface PrescriptionsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(chatMessages: List<PrescriptionsCacheEntity>)

    @Query("SELECT * FROM Prescriptions WHERE id = :id")
    fun get(id: String): PrescriptionsCacheEntity
}