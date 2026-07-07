package com.sae.wavetime.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsTracker(context: Context) : AnalyticsLogger {

    private val analytics = FirebaseAnalytics.getInstance(context.applicationContext)

    override fun logTaskCreated(completeMode: String, taskType: String) {
        val params = Bundle().apply {
            putString("completeMode", completeMode)
            putString("task_type", taskType)
        }

        analytics.logEvent("task_created", params)
    }

    override fun logTaskCompleted(completeMode: String, taskType: String) {
        val params = Bundle().apply {
            putString("completeMode", completeMode)
            putString("task_type", taskType)
        }

        analytics.logEvent("task_completed", params)
    }

    override fun logRewardGenerated(hasItemReward: Boolean) {
        val params = Bundle().apply {
            putBoolean("has_item_reward", hasItemReward)
        }

        analytics.logEvent("reward_generated", params)
    }

    override fun logItemUsedUnlock(itemCategory: String) {
        val params = Bundle().apply {
            putString("item_type", itemCategory)
        }

        analytics.logEvent("item_used_unlock", params)
    }

    override fun logBlockTriggered(blockType: String, isTemporarilyUnlocked: Boolean) {
        val params = Bundle().apply {
            putString("block_type", blockType)
            putBoolean("is_temporarily_unlocked", isTemporarilyUnlocked)
        }

        analytics.logEvent("block_triggered", params)
    }

    override fun logBlockTemporarilyUnlocked(durationMinutes: Int) {
        val params = Bundle().apply {
            putInt("duration_minutes", durationMinutes)
        }

        analytics.logEvent("block_temporarily_unlocked", params)
    }

    override fun logBlockRelocked(reason: String) {
        val params = Bundle().apply {
            putString("reason", reason)
        }

        analytics.logEvent("block_relocked", params)
    }
}