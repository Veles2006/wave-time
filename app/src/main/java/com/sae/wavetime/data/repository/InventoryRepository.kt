package com.sae.wavetime.data.repository

import com.sae.wavetime.data.dao.InventoryDao
import com.sae.wavetime.data.mapper.toEntity
import com.sae.wavetime.data.mapper.toUi
import com.sae.wavetime.data.model.entity.InventoryEntity
import com.sae.wavetime.domain.model.Inventory
import com.sae.wavetime.domain.model.RewardItem
import com.sae.wavetime.ui.model.InventoryUiModel
import java.util.UUID

class InventoryRepository(
    private val inventoryDao: InventoryDao
) {
    suspend fun getInventoryItems() : List<InventoryUiModel> {
        val data = inventoryDao.getInventoryWithItem()

        return data.map { it.toUi() }
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

    suspend fun subtractQuantity(rewards: List<RewardItem>){
        rewards.forEach { item ->
            inventoryDao.subtractQuantity(
                itemId = item.itemId.id,
                amount = item.quantity
            )
        }
    }

    suspend fun insertInventory(inventory: Inventory) {
        inventoryDao.insert(inventory.toEntity())
    }
}