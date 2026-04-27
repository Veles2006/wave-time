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
        amount: Int
    ) {
        database.withTransaction {
            inventoryRepo.subtractQuantity(itemId, amount)
        }
    }
}