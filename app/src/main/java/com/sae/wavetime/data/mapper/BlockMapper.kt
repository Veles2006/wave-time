package com.sae.wavetime.data.mapper

import com.sae.wavetime.domain.model.Block
import com.sae.wavetime.data.model.entity.BlockEntity
import com.sae.wavetime.ui.model.AppUiModel

fun Block.toEntity(): BlockEntity {
    return BlockEntity(
        id = id,
        appName = appName,
        packageName = packageName,
        blockType = blockType,
        penaltyMinutes = penaltyMinutes,
        isActive = isActive,
        unlockUntil = unlockUntil,
        isDeleted = isDeleted
    )
}

fun BlockEntity.toDomain(): Block {
    return Block(
        id = id,
        appName = appName,
        packageName = packageName,
        blockType = blockType,
        penaltyMinutes = penaltyMinutes,
        isActive = isActive,
        unlockUntil = unlockUntil,
        isDeleted = isDeleted
    )
}

fun BlockEntity.toUi(): AppUiModel {
    return AppUiModel(
        id = id,
        appName = appName,
        packageName = packageName
    )
}

fun AppUiModel.toEntity(): BlockEntity {
    return BlockEntity(
        id = id,
        appName = appName,
        packageName = packageName,
        blockType = blockType,
        penaltyMinutes = penaltyMinutes
    )
}

fun AppUiModel.toDomain(): Block {
    return Block(
        id = id,
        appName = appName,
        packageName = packageName,
        blockType = blockType,
        penaltyMinutes = penaltyMinutes
    )
}

fun List<Block>.toEntityList(): List<BlockEntity> {
    return map { it.toEntity() }
}

fun List<BlockEntity>.toDomainList(): List<Block> {
    return map { it.toDomain() }
}