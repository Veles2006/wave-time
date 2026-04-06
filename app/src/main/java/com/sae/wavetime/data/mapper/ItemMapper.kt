package com.sae.wavetime.data.mapper

import com.sae.wavetime.data.model.api.Item
import com.sae.wavetime.data.model.entity.ItemEntity
import com.sae.wavetime.ui.model.RewardSelectUiModel

fun Item.toEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        name = name,
        tier = tier,
        rank = rank,
        category = category,
        keyInfo = keyInfo,
        description = description,
        icon = icon
    )
}

fun ItemEntity.toDomain(): Item {
    return Item(
        id = id,
        name = name,
        tier = tier,
        rank = rank,
        category = category,
        keyInfo = keyInfo,
        description = description,
        icon = icon
    )
}

fun ItemEntity.toRewardSelect(): RewardSelectUiModel {
    return RewardSelectUiModel(
        id = id,
        name = name,
        tier = tier
    )
}

fun List<Item>.toEntityList(): List<ItemEntity> {
    return map { it.toEntity() }
}

fun List<ItemEntity>.toDomainList(): List<Item> {
    return map { it.toDomain() }
}

fun List<ItemEntity>.toRewardSelectList(): List<RewardSelectUiModel> {
    return map { it.toRewardSelect() }
}