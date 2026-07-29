package com.sae.wavetime.domain.usecase

import androidx.room.withTransaction
import com.sae.wavetime.engine.block.BlockReactivationScheduler
import com.sae.wavetime.local.AppDatabase

class DeleteBlockUseCase(
    private val database: AppDatabase,
    private val scheduler: BlockReactivationScheduler
) {

    suspend fun execute(blockId: String) {
        val deletedRows = database.withTransaction {
            database.blockDao()
                .softDelete(blockId)
        }

        if (deletedRows > 0) {
            scheduler.cancel(blockId)
        }
    }
}