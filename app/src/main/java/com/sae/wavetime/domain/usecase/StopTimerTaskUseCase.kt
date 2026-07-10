package com.sae.wavetime.domain.usecase

import android.content.Context
import com.sae.wavetime.data.repository.TaskRepository
import com.sae.wavetime.engine.alarm.TaskTimerAlarmScheduler
import com.sae.wavetime.engine.service.TaskTimerService

class StopTimerTaskUseCase(
    private val context: Context,
    private val taskRepo: TaskRepository
) {
    suspend fun execute(taskId: String) {
        TaskTimerAlarmScheduler.cancel(
            context = context,
            taskId = taskId
        )

        TaskTimerService.stop(context)

        val stopped = taskRepo.stopTimerTask(taskId)

        if (!stopped) {
            throw IllegalStateException(
                "Task is not running or no longer exists"
            )
        }
    }
}