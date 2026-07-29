package com.sae.wavetime.engine.block

import androidx.room.withTransaction
import com.sae.wavetime.local.AppDatabase

class BlockReactivationReconciler(
    private val database: AppDatabase,
    private val scheduler: BlockReactivationScheduler
) {

    suspend fun reconcile() {
        val now = System.currentTimeMillis()

        database.withTransaction {
            database.blockDao()
                .reactivateAllDueBlocks(now)
        }

        val pendingBlocks = database.blockDao()
            .getPendingReactivations()

        pendingBlocks.forEach { block ->
            if (
                !block.isDeleted &&
                !block.isActive &&
                block.reactivateAt > now
            ) {
                scheduler.schedule(
                    blockId = block.id,
                    reactivateAt = block.reactivateAt
                )
            }
        }
    }
}