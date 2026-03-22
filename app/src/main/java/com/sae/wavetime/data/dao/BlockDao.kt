package com.sae.wavetime.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sae.wavetime.data.model.entity.BlockEntity

@Dao
interface BlockDao {
    @Query("SELECT * FROM blocks")
    suspend fun getAll(): List<BlockEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: BlockEntity)

    @Delete
    suspend fun delete(block: BlockEntity)
}