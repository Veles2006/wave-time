package com.sae.wavetime.data.repository

import com.sae.wavetime.data.dao.InventoryDao
import com.sae.wavetime.data.mapper.toUi
import com.sae.wavetime.ui.model.InventoryUiModel

class InventoryRepository(
    private val inventoryDao: InventoryDao
) {
    suspend fun getInventoryItems() : List<InventoryUiModel> {
        val data = inventoryDao.getInventoryWithItem()

        return data.map { it.toUi() }
    }
}