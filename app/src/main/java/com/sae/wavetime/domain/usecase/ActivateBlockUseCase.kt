package com.sae.wavetime.domain.usecase

import androidx.room.withTransaction
import com.sae.wavetime.engine.block.BlockReactivationScheduler
import com.sae.wavetime.local.AppDatabase

class ActivateBlockUseCase(
    private val database: AppDatabase,
    private val scheduler: BlockReactivationScheduler
) {

    suspend fun execute(blockId: String) {
        val updatedRows = database.withTransaction {
            database.blockDao()
                .activateBlock(blockId)
        }

        if (updatedRows > 0) {
            scheduler.cancel(blockId)
        }
    }
}