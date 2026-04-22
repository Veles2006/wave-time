package com.sae.wavetime.domain.usecase

import android.util.Log
import androidx.room.withTransaction
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
        rewards: List<RewardItem>
    ) {
        database.withTransaction {
            Log.d("TEST", "$taskId")
            Log.d("TEST", "$rewards")
            taskRepo.changeStatus(taskId, "completed")
            inventoryRepo.addQuantity(rewards)
        }
    }
}