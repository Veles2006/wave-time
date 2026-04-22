package com.sae.wavetime.data.mapper

import com.sae.wavetime.domain.model.Inventory
import com.sae.wavetime.data.model.entity.InventoryEntity
import com.sae.wavetime.data.model.relation.InventoryWithItem
import com.sae.wavetime.ui.model.InventoryUiModel

fun InventoryWithItem.toUi(): InventoryUiModel {
    return InventoryUiModel(
        id = item.id,
        name = item.name,
        icon = item.icon,
        tier = item.tier,
        quantity = inventory.quantity
    )
}

fun Inventory.toEntity(): InventoryEntity {
    return InventoryEntity(
        id = id,
        itemId = itemId.id,
        quantity = quantity
    )
}