package com.sae.wavetime.data.mapper

import com.sae.wavetime.domain.model.Item
import com.sae.wavetime.domain.model.RewardItem
import com.sae.wavetime.domain.model.RewardItemId
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
        tier = tier,
        rank = rank,
        category =category,
        description = description,
        icon = icon
    )
}

fun RewardSelectUiModel.toRewardItem() : RewardItem {
    return RewardItem(
        itemId = RewardItemId(
            id,
            name,
            tier,
            rank,
            category,
            description,
            icon
        ),
        quantity = quantity
    )
}

fun RewardItem.toRewardSelectUiModel() : RewardSelectUiModel {
    return RewardSelectUiModel(
        id = itemId.id,
        name = itemId.name,
        tier = itemId.tier,
        rank = itemId.rank,
        category = itemId.category,
        description = itemId.description,
        icon = itemId.icon,
        quantity = quantity
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

fun List<RewardSelectUiModel>.toRewardItemList(): List<RewardItem> {
    return map { it.toRewardItem() }
}

fun List<RewardItem>.toRewardSelectUiModelList(): List<RewardSelectUiModel> {
    return map { it.toRewardSelectUiModel() }
}