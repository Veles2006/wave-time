package com.sae.wavetime.data.mapper

import com.sae.wavetime.domain.model.Inventory
import com.sae.wavetime.data.model.entity.InventoryEntity
import com.sae.wavetime.data.model.relation.InventoryWithItem
import com.sae.wavetime.ui.model.InventoryDetailUiModel
import com.sae.wavetime.ui.model.InventoryUiModel

fun InventoryWithItem.toListUi(): InventoryUiModel {
    return InventoryUiModel(
        id = item.id,
        name = item.name,
        tier = item.tier,
        quantity = inventory.quantity
    )
}

fun InventoryWithItem.toDetailUi(): InventoryDetailUiModel {
    return InventoryDetailUiModel(
        inventoryId = inventory.id,
        itemId = item.id,
        name = item.name,
        tier = item.tier,
        rank = item.rank,
        category = item.category,
        blockId = item.blockId,
        durationMinutes = item.keyInfo.durationMinutes,
        isMaster = item.keyInfo.isMaster,
        description = item.description,
        quantity = inventory.quantity,
    )
}

fun Inventory.toEntity(): InventoryEntity {
    return InventoryEntity(
        id = id,
        itemId = itemId.id,
        quantity = quantity
    )
}