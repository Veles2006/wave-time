package com.sae.wavetime.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sae.wavetime.data.model.entity.InstalledAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstalledAppDao {

    @Query("SELECT * FROM installed_apps ORDER BY appName ASC")
    fun getAll(): Flow<List<InstalledAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(apps: List<InstalledAppEntity>)

    @Query("DELETE FROM installed_apps")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM installed_apps")
    suspend fun countAll(): Int

    @Query("SELECT * FROM installed_apps LIMIT 5")
    suspend fun getFirstFive(): List<InstalledAppEntity>
}