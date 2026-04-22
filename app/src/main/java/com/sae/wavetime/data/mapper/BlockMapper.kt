package com.sae.wavetime.data.mapper

import com.sae.wavetime.domain.model.Block
import com.sae.wavetime.data.model.entity.BlockEntity

fun Block.toEntity(): BlockEntity {
    return BlockEntity(
        id = id,
        appName = appName,
        packageName = packageName,
        blockType = blockType,
        penaltyMinutes = penaltyMinutes,
        isActive = isActive
    )
}

fun BlockEntity.toDomain(): Block {
    return Block(
        id = id,
        appName = appName,
        packageName = packageName,
        blockType = blockType,
        penaltyMinutes = penaltyMinutes,
        isActive = isActive
    )
}

fun List<Block>.toEntityList(): List<BlockEntity> {
    return map { it.toEntity() }
}

fun List<BlockEntity>.toDomainList(): List<Block> {
    return map { it.toDomain() }
}