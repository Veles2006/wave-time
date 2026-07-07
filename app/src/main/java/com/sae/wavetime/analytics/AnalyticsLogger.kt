package com.sae.wavetime.analytics

interface AnalyticsLogger {
    // Task
    fun logTaskCreated(
        completeMode: String,
        taskType: String
    )

    fun logTaskCompleted(
        completeMode: String,
        taskType: String
    )

    // Reward
    fun logRewardGenerated(
        hasItemReward: Boolean
    )

    // Item
    fun logItemUsedUnlock(
        itemCategory: String
    )

    // Block
    fun logBlockTriggered(
        blockType: String,
        isTemporarilyUnlocked: Boolean
    )

    fun logBlockTemporarilyUnlocked(
        durationMinutes: Int
    )

    fun logBlockRelocked(
        reason: String
    )
}