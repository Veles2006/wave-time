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

    @Query(
        """
    SELECT * FROM blocks
    WHERE isDeleted = 0
      AND (
          isActive = 1
          OR (
              blockType = 'permanent'
              AND reactivateAt > 0
          )
      )
    """
    )
    fun observeBlockCandidates(): Flow<List<BlockEntity>>

    @Query("""
    SELECT * FROM blocks
    WHERE isDeleted = 1
""")
    fun observeDeletedBlocks(): Flow<List<BlockEntity>>

    @Query("SELECT * FROM blocks WHERE packageName = :packageName LIMIT 1")
    suspend fun getByPackageName(packageName: String): BlockEntity?

    @Query("""
    SELECT appName
    FROM blocks
    WHERE id = :blockId
    LIMIT 1
""")
    fun observeBlockNameById(
        blockId: String
    ): Flow<String?>

    @Query(
        """
        SELECT * FROM blocks
        WHERE isDeleted = 0
          AND blockType = 'permanent'
          AND isActive = 0
          AND reactivateAt > 0
        """
    )
    suspend fun getPendingReactivations(): List<BlockEntity>

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

    @Query(
        """
        UPDATE blocks
        SET isActive = 0,
            reactivateAt = :reactivateAt
        WHERE id = :blockId
          AND isDeleted = 0
          AND blockType = 'permanent'
          AND isActive = 1
        """
    )
    suspend fun temporarilyDeactivatePermanentBlock(
        blockId: String,
        reactivateAt: Long
    ): Int

    @Query(
        """
        UPDATE blocks
        SET isActive = 1,
            reactivateAt = 0
        WHERE id = :blockId
          AND isDeleted = 0
          AND blockType = 'permanent'
          AND isActive = 0
          AND reactivateAt > 0
          AND reactivateAt <= :now
        """
    )
    suspend fun reactivateBlockIfDue(
        blockId: String,
        now: Long
    ): Int

    @Query(
        """
        UPDATE blocks
        SET isActive = 1,
            reactivateAt = 0
        WHERE isDeleted = 0
          AND blockType = 'permanent'
          AND isActive = 0
          AND reactivateAt > 0
          AND reactivateAt <= :now
        """
    )
    suspend fun reactivateAllDueBlocks(now: Long): Int

    @Query("UPDATE blocks SET isDeleted = 0 WHERE id = :id")
    suspend fun restoreBlock(id: String)

    @Query("UPDATE blocks SET isActive = :isActive WHERE id = :id")
    suspend fun toggleActive(id: String, isActive: Boolean)

    @Query(
        """
    UPDATE blocks
    SET isActive = 1,
        reactivateAt = 0
    WHERE id = :blockId
      AND isDeleted = 0
    """
    )
    suspend fun activateBlock(blockId: String): Int

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
    @Query(
        """
        UPDATE blocks
        SET isDeleted = 1,
            isActive = 0,
            reactivateAt = 0
        WHERE id = :id
          AND isDeleted = 0
        """
    )
    suspend fun softDelete(id: String): Int
}