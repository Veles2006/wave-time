package com.sae.wavetime.data.mapper

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