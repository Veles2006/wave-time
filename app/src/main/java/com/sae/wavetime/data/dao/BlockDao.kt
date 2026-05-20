package com.sae.wavetime.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sae.wavetime.data.model.entity.BlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {
    //Get method
    @Query("SELECT * FROM blocks WHERE isDeleted = 0")
    fun getAllFlow(): Flow<List<BlockEntity>>

    @Query("SELECT * FROM blocks WHERE id = :id LIMIT 1")
    fun getByIdFlow(id: String): Flow<BlockEntity?>

    @Query("""
    SELECT * FROM blocks
    WHERE isDeleted = 0
    AND isActive = 1
""")
    fun observeActiveBlocks(): Flow<List<BlockEntity>>

    @Query("SELECT * FROM blocks WHERE packageName = :packageName LIMIT 1")
    suspend fun getByPackageName(packageName: String): BlockEntity?

    // Create method
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: BlockEntity)

    // Update method
    @Query("""
        UPDATE blocks
        SET appName = :appName,
            packageName = :packageName,
            blockType = :blockType,
            penaltyMinutes = :penaltyMinutes,
            isActive = :isActive,
            isDeleted = :isDeleted
        WHERE id = :id
    """)
    suspend fun updateFull(
        id: String,
        appName: String,
        packageName: String,
        blockType: String,
        penaltyMinutes: Int,
        isActive: Boolean,
        isDeleted: Boolean
    )

    @Query("UPDATE blocks SET isDeleted = 0 WHERE id = :id")
    suspend fun restoreBlock(id: String)

    @Query("UPDATE blocks SET isActive = :isActive WHERE id = :id")
    suspend fun toggleActive(id: String, isActive: Boolean)

    @Query("""
    UPDATE blocks
    SET unlockUntil = :unlockUntil
    WHERE id = :id
""")
    suspend fun setUnlockUntil(
        id: String,
        unlockUntil: Long
    )

    // Delete method
    @Query("UPDATE blocks SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: String)
}