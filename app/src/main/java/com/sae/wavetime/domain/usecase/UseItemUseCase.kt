package com.sae.wavetime.domain.usecase

import androidx.room.withTransaction
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.ItemRepository
import com.sae.wavetime.local.AppDatabase

class UseItemUseCase(
    private val itemRepo: ItemRepository,
    private val inventoryRepo: InventoryRepository,
    private val blockRepo: BlockRepository,
    private val database: AppDatabase
) {
    suspend fun execute(
        itemId: String,
        amount: Int = 1
    ) {
        database.withTransaction {
            val item = itemRepo.getItemById(itemId)
                ?: error("Item not found")

            if (item.category != "key") {
                error("This item is not a key")
            }

            val keyInfo = item.keyInfo

            if (keyInfo.durationMinutes <= 0) {
                error("Key duration invalid")
            }

            val blockId = keyInfo.blockId?.id
                ?: error("Key chưa gắn với block nào")

            inventoryRepo.subtractQuantity(itemId, amount)

            blockRepo.unlockTemporarily(
                blockId = blockId,
                durationMinutes = keyInfo.durationMinutes
            )
        }
    }
}