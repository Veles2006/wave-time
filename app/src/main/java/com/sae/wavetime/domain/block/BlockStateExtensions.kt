package com.sae.wavetime.domain.block

import com.sae.wavetime.data.model.entity.BlockEntity
import com.sae.wavetime.domain.model.Block

fun BlockEntity.shouldBeActive(
    now: Long = System.currentTimeMillis()
): Boolean {
    if (isDeleted) {
        return false
    }

    if (isActive) {
        return true
    }

    return blockType == "permanent" &&
            reactivateAt > 0L &&
            reactivateAt <= now
}

fun BlockEntity.shouldBlock(
    now: Long = System.currentTimeMillis()
): Boolean {
    return shouldBeActive(now) &&
            unlockUntil <= now
}

fun BlockEntity.isReactivationDue(
    now: Long = System.currentTimeMillis()
): Boolean {
    return !isDeleted &&
            blockType == "permanent" &&
            !isActive &&
            reactivateAt > 0L &&
            reactivateAt <= now
}

fun Block.shouldBeActive(
    now: Long = System.currentTimeMillis()
): Boolean {
    if (isDeleted) {
        return false
    }

    if (isActive) {
        return true
    }

    return blockType == "permanent" &&
            reactivateAt > 0L &&
            reactivateAt <= now
}

fun Block.shouldBlock(
    now: Long = System.currentTimeMillis()
): Boolean {
    return shouldBeActive(now) &&
            unlockUntil <= now
}

fun Block.isReactivationDue(
    now: Long = System.currentTimeMillis()
): Boolean {
    return !isDeleted &&
            blockType == "permanent" &&
            !isActive &&
            reactivateAt > 0L &&
            reactivateAt <= now
}