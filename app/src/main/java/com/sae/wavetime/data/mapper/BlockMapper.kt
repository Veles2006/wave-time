package com.sae.wavetime.data.mapper

import android.R.attr.description
import android.R.attr.name
import android.graphics.drawable.Drawable
import com.sae.wavetime.data.model.api.Block
import com.sae.wavetime.data.model.api.Task
import com.sae.wavetime.data.model.entity.BlockEntity
import com.sae.wavetime.data.model.entity.TaskEntity
import com.sae.wavetime.ui.model.AppUiModel

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