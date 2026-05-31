package com.sae.wavetime.domain.usecase

import android.util.Log
import androidx.room.withTransaction
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.domain.model.Task
import com.sae.wavetime.local.AppDatabase

class StartTimerTaskUseCase(
    private val taskRepo: TaskRepository,
    private val database: AppDatabase
) {
    suspend fun execute(task: Task): Long {
        return database.withTransaction {
            val now = System.currentTimeMillis()

            val runningTask = taskRepo.getRunningTimerTask()

            if (runningTask != null && runningTask.id != task.id) {
                error("Đang có task timer khác chạy")
            }

            if (task.finishAt != null && task.finishAt > now) {
                error("Task này đang chạy timer rồi")
            }

            val timerMinutes = task.requiredDurationMinutes
                ?: error("Timer duration is required")

            if (timerMinutes <= 0) {
                error("Timer duration invalid")
            }

            val durationMillis = timerMinutes * 60_000L
            val finishAt = now + durationMillis

            taskRepo.startTimerTask(
                taskId = task.id,
                startedAt = now,
                finishAt = finishAt
            )

            finishAt
        }
    }
}