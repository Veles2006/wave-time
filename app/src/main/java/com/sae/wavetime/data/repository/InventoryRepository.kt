package com.sae.wavetime.data.repository

import com.sae.wavetime.data.dao.InventoryDao
import com.sae.wavetime.data.mapper.toEntity
import com.sae.wavetime.data.mapper.toUi
import com.sae.wavetime.data.model.entity.InventoryEntity
import com.sae.wavetime.domain.model.Inventory
import com.sae.wavetime.domain.model.RewardItem
import com.sae.wavetime.ui.model.InventoryUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.UUID

class InventoryRepository(
    private val inventoryDao: InventoryDao
) {
    fun getInventoryItems() : Flow<List<InventoryUiModel>> {
        val data = inventoryDao.getInventoryWithItem()

        return data
            .map { list ->
                list
                    .filter { it.inventory.quantity > 0 }
                    .map { it.toUi() }
            }
    }

    suspend fun getByItemId(itemId: String): InventoryEntity? {
        return inventoryDao.getByItemId(itemId)
    }

    suspend fun addQuantity(rewards: List<RewardItem>){
        rewards.forEach { item ->
            if (inventoryDao.getByItemId(item.itemId.id) == null) {
                inventoryDao.insert(
                    InventoryEntity(
                        id = UUID.randomUUID().toString(),
                        itemId = item.itemId.id,
                        quantity = item.quantity
                    )
                )
            } else {
                inventoryDao.addQuantity(
                    itemId = item.itemId.id,
                    amount = item.quantity
                )
            }
        }
    }

    suspend fun subtractQuantity(itemId: String, amount: Int){
        inventoryDao.subtractQuantity(
            itemId = itemId,
            amount = amount
        )
    }

    suspend fun insertInventory(inventory: Inventory) {
        inventoryDao.insert(inventory.toEntity())
    }
}