package com.sae.wavetime.data.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.sae.wavetime.data.model.entity.InventoryEntity
import com.sae.wavetime.data.model.entity.ItemEntity

data class InventoryWithItem(
    @Embedded val inventory: InventoryEntity,

    @Relation(
        parentColumn = "itemId",
        entityColumn = "id"
    )

    val item: ItemEntity
)
