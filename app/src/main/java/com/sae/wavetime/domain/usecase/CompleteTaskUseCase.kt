package com.sae.wavetime.domain.usecase

import android.util.Log
import androidx.room.withTransaction
import com.sae.wavetime.data.repository.BlockRepository
import com.sae.wavetime.data.repository.InventoryRepository
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.model.RewardItem
import com.sae.wavetime.local.AppDatabase

class CompleteTaskUseCase(
    private val taskRepo: TaskRepository,
    private val inventoryRepo: InventoryRepository,
    private val database: AppDatabase
) {
    suspend fun execute(
        taskId: String,
    ) {
        database.withTransaction {
            val task = taskRepo.getTaskById(taskId)
                ?: return@withTransaction

            if (task.status == "completed") {
                return@withTransaction
            }
            val rewards = task.reward.items
            taskRepo.changeStatus(taskId, "completed", System.currentTimeMillis())
            inventoryRepo.addQuantity(rewards)
        }
    }
}