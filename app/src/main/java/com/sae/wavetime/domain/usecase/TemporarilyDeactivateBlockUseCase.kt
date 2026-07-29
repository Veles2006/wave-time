package com.sae.wavetime.domain.usecase

import android.util.Log
import androidx.room.withTransaction
import com.sae.wavetime.engine.block.BlockReactivationScheduler
import com.sae.wavetime.local.AppDatabase

class TemporarilyDeactivateBlockUseCase(
    private val database: AppDatabase,
    private val scheduler: BlockReactivationScheduler
) {

    suspend fun execute(
        blockId: String,
        durationMillis: Long = DEFAULT_DURATION_MILLIS
    ) {
        val now = System.currentTimeMillis()
        val reactivateAt = now + durationMillis

        val updatedRows = database.withTransaction {
            database.blockDao()
                .temporarilyDeactivatePermanentBlock(
                    blockId = blockId,
                    reactivateAt = reactivateAt
                )
        }

        if (updatedRows == 0) {
            return
        }

        /*
         * Schedule sau khi Room đã cập nhật thành công.
         *
         * Nếu schedule thất bại, reactivateAt vẫn còn trong DB
         * và Reconciler/AccessibilityService sẽ phục hồi.
         */

        scheduler.schedule(
            blockId = blockId,
            reactivateAt = reactivateAt
        )
    }

    companion object {
        private const val DEFAULT_DURATION_MILLIS =
            1L * 60L * 1000L
    }
}